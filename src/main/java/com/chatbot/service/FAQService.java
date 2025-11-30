package com.chatbot.service;

import com.chatbot.dto.FAQRequest;
import com.chatbot.entity.FAQ;
import com.chatbot.nlp.IntentClassifier;
import com.chatbot.repository.FAQRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class FAQService {

    private final FAQRepository faqRepository;
    private final IntentClassifier intentClassifier;
    private final ObjectMapper objectMapper;

    public FAQService(FAQRepository faqRepository, IntentClassifier intentClassifier, ObjectMapper objectMapper) {
        this.faqRepository = faqRepository;
        this.intentClassifier = intentClassifier;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public FAQ createOrUpdateFAQ(FAQRequest request) {
        log.info("Creating/updating FAQ for intent: {}", request.getIntent());

        FAQ faq = faqRepository.findByIntent(request.getIntent())
                .orElse(FAQ.builder().intent(request.getIntent()).build());

        try {
            faq.setPatterns(objectMapper.writeValueAsString(request.getPatterns()));
            faq.setResponses(objectMapper.writeValueAsString(request.getResponses()));
            faq.setContextRequired(request.getContextRequired());
            
            if (request.getQuickReplies() != null && !request.getQuickReplies().isEmpty()) {
                faq.setQuickReplies(objectMapper.writeValueAsString(request.getQuickReplies()));
            }
            
            faq.setPriority(request.getPriority() != null ? request.getPriority() : 0);
            faq.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

            FAQ saved = faqRepository.save(faq);
            
            // Reload intents in classifier
            intentClassifier.loadIntents();
            
            return saved;
        } catch (Exception e) {
            log.error("Error saving FAQ", e);
            throw new RuntimeException("Failed to save FAQ", e);
        }
    }

    public List<FAQ> getAllFAQs() {
        return faqRepository.findAllByOrderByPriorityDesc();
    }

    public FAQ getFAQByIntent(String intent) {
        return faqRepository.findByIntent(intent)
                .orElseThrow(() -> new IllegalArgumentException("FAQ not found: " + intent));
    }

    @Transactional
    public void deleteFAQ(String id) {
        faqRepository.deleteById(id);
        intentClassifier.loadIntents();
    }
}
