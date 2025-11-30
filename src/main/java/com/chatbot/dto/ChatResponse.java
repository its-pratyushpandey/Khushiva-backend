package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String messageId;
    private String sessionId;
    private String response;
    private String intent;
    private Double confidence;
    private List<String> quickReplies;
    private List<EntityInfo> entities;
    private String source; // "rule", "nlp", "llm"
    private LocalDateTime timestamp;
    private boolean requiresFollowup;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityInfo {
        private String type;
        private String value;
        private Integer startPosition;
        private Integer endPosition;
    }
}
