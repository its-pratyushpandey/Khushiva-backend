package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @Test
    void testSendMessage() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .sessionId("test-session")
                .message("Hello")
                .userIdentifier("test-user")
                .build();

        ChatResponse response = ChatResponse.builder()
                .messageId("msg-001")
                .sessionId("test-session")
                .response("Hello! How can I help you?")
                .intent("greeting")
                .confidence(0.95)
                .quickReplies(Arrays.asList("Help", "Pricing"))
                .source("nlp")
                .timestamp(LocalDateTime.now())
                .build();

        when(chatService.processMessage(any(ChatRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("test-session"))
                .andExpect(jsonPath("$.intent").value("greeting"))
                .andExpect(jsonPath("$.confidence").value(0.95))
                .andExpect(jsonPath("$.source").value("nlp"));
    }

    @Test
    void testSendMessageWithInvalidRequest() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .sessionId("")
                .message("")
                .build();

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
