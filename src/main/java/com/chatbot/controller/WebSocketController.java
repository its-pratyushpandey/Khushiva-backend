package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.dto.TypingEvent;
import com.chatbot.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatResponse sendMessage(@Payload ChatRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.info("WebSocket message received for session: {}", request.getSessionId());
        
        try {
            ChatResponse response = chatService.processMessage(request);
            
            // Send response to specific session
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + request.getSessionId(),
                    response
            );
            
            return response;
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            return ChatResponse.builder()
                    .sessionId(request.getSessionId())
                    .response("Sorry, I encountered an error processing your message.")
                    .build();
        }
    }

    @MessageMapping("/chat.typing")
    @SendTo("/topic/typing")
    public TypingEvent handleTyping(@Payload TypingEvent event) {
        log.debug("Typing event for session: {}", event.getSessionId());
        return event;
    }
}
