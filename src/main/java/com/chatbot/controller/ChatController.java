package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.dto.SessionDTO;
import com.chatbot.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat", description = "Chat conversation endpoints")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Send a message", description = "Send a message and receive AI-generated response")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request for session: {}", request.getSessionId());
        ChatResponse response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get session history", description = "Retrieve conversation history for a session")
    public ResponseEntity<SessionDTO> getSessionHistory(@PathVariable String sessionId) {
        log.info("Fetching session history: {}", sessionId);
        SessionDTO session = chatService.getSessionHistory(sessionId);
        return ResponseEntity.ok(session);
    }
}
