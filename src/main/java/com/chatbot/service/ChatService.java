package com.chatbot.service;

import com.chatbot.dto.*;
import com.chatbot.entity.ChatMessage;
import com.chatbot.entity.ChatSession;
import com.chatbot.entity.FAQ;
import com.chatbot.llm.LLMAdapter;
import com.chatbot.nlp.ContextManager;
import com.chatbot.nlp.EntityExtractor;
import com.chatbot.nlp.IntentClassifier;
import com.chatbot.nlp.IntentResult;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.ChatSessionRepository;
import com.chatbot.repository.FAQRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final FAQRepository faqRepository;
    private final IntentClassifier intentClassifier;
    private final EntityExtractor entityExtractor;
    private final ContextManager contextManager;
    private final LLMAdapter llmAdapter;
    private final ObjectMapper objectMapper;
    private final Random random;

    public ChatService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository,
            FAQRepository faqRepository,
            IntentClassifier intentClassifier,
            EntityExtractor entityExtractor,
            ContextManager contextManager,
            LLMAdapter llmAdapter,
            ObjectMapper objectMapper
    ) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.faqRepository = faqRepository;
        this.intentClassifier = intentClassifier;
        this.entityExtractor = entityExtractor;
        this.contextManager = contextManager;
        this.llmAdapter = llmAdapter;
        this.objectMapper = objectMapper;
        this.random = new Random();
    }

    @Transactional
    public ChatResponse processMessage(ChatRequest request) {
        log.info("Processing message for session: {}", request.getSessionId());

        // Get or create session
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseGet(() -> createSession(request.getSessionId(), request.getUserIdentifier()));

        // Create user message
        ChatMessage userMessage = ChatMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .sessionId(session.getId())
                .content(request.getMessage())
                .senderType(ChatMessage.SenderType.USER)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Add to session
        session.getMessages().add(userMessage);

        // Process with NLP pipeline
        String sanitizedInput = sanitizeInput(request.getMessage());
        IntentResult intentResult = intentClassifier.classify(sanitizedInput);
        List<IntentResult.Entity> entities = entityExtractor.extract(sanitizedInput);
        intentResult.setEntities(entities);

        // Update context
        contextManager.updateContext(session.getId(), "last_intent", intentResult.getIntent());

        // Generate response
        String responseText;
        String source;
        List<String> quickReplies = new ArrayList<>();

        if (intentClassifier.meetsThreshold(intentResult.getConfidence())) {
            // High confidence - use FAQ response
            FAQ faq = faqRepository.findByIntent(intentResult.getIntent()).orElse(null);
            if (faq != null) {
                responseText = selectRandomResponse(faq.getResponses());
                quickReplies = parseQuickReplies(faq.getQuickReplies());
                source = "nlp";
            } else {
                responseText = getFallbackResponse();
                source = "rule";
            }
        } else if (llmAdapter.isLLMEnabled()) {
            // Low confidence but LLM available
            String context = buildContext(session);
            responseText = llmAdapter.getResponse(sanitizedInput, context);
            if (responseText == null) {
                responseText = getFallbackResponse();
                source = "rule";
            } else {
                source = "llm";
            }
        } else {
            // Low confidence and no LLM
            responseText = getFallbackResponse();
            source = "rule";
        }

        // Save bot response
        ChatMessage botMessage = ChatMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .sessionId(session.getId())
                .content(responseText)
                .senderType(ChatMessage.SenderType.BOT)
                .intent(intentResult.getIntent())
                .confidenceScore(intentResult.getConfidence())
                .entities(entitiesToJson(entities))
                .createdAt(LocalDateTime.now())
                .build();
        
        // Add to session and save
        session.getMessages().add(botMessage);
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Build response DTO
        return ChatResponse.builder()
                .messageId(botMessage.getId())
                .sessionId(session.getId())
                .response(responseText)
                .intent(intentResult.getIntent())
                .confidence(intentResult.getConfidence())
                .quickReplies(quickReplies)
                .entities(mapEntities(entities))
                .source(source)
                .timestamp(LocalDateTime.now())
                .requiresFollowup(false)
                .build();
    }

    public SessionDTO getSessionHistory(String sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        List<MessageDTO> messages = session.getMessages().stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());

        return SessionDTO.builder()
                .id(session.getId())
                .userIdentifier(session.getUserIdentifier())
                .isActive(session.getIsActive())
                .messages(messages)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .lastActivityAt(session.getLastActivityAt())
                .build();
    }

    private ChatSession createSession(String sessionId, String userIdentifier) {
        ChatSession session = ChatSession.builder()
                .id(sessionId)
                .userIdentifier(userIdentifier != null ? userIdentifier : "anonymous")
                .isActive(true)
                .lastActivityAt(LocalDateTime.now())
                .build();
        return sessionRepository.save(session);
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim()
                .replaceAll("<script>.*?</script>", "")
                .replaceAll("<.*?>", "")
                .substring(0, Math.min(input.length(), 500));
    }

    private String selectRandomResponse(String responsesJson) {
        try {
            List<String> responses = objectMapper.readValue(responsesJson, new TypeReference<>() {});
            return responses.get(random.nextInt(responses.size()));
        } catch (Exception e) {
            log.error("Error parsing responses", e);
            return "I'm here to help!";
        }
    }

    private List<String> parseQuickReplies(String quickRepliesJson) {
        if (quickRepliesJson == null || quickRepliesJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(quickRepliesJson, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error parsing quick replies", e);
            return new ArrayList<>();
        }
    }

    private String getFallbackResponse() {
        List<String> fallbacks = List.of(
                "I'm not quite sure I understand. Could you rephrase that?",
                "I'm still learning. Can you try asking in a different way?",
                "I didn't catch that. Could you provide more details?",
                "That's a great question! Let me connect you with someone who can help better."
        );
        return fallbacks.get(random.nextInt(fallbacks.size()));
    }

    private String buildContext(ChatSession session) {
        List<ChatMessage> recentMessages = session.getMessages().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        StringBuilder context = new StringBuilder("Recent conversation:\n");
        for (ChatMessage msg : recentMessages) {
            context.append(msg.getSenderType()).append(": ").append(msg.getContent()).append("\n");
        }
        return context.toString();
    }

    private String entitiesToJson(List<IntentResult.Entity> entities) {
        try {
            return objectMapper.writeValueAsString(entities);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<ChatResponse.EntityInfo> mapEntities(List<IntentResult.Entity> entities) {
        return entities.stream()
                .map(e -> ChatResponse.EntityInfo.builder()
                        .type(e.getType())
                        .value(e.getValue())
                        .startPosition(e.getStart())
                        .endPosition(e.getEnd())
                        .build())
                .collect(Collectors.toList());
    }

    private MessageDTO mapToMessageDTO(ChatMessage message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderType(message.getSenderType().name())
                .intent(message.getIntent())
                .confidenceScore(message.getConfidenceScore())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
