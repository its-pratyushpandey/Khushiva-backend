package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;
    private String version;
    private DatabaseStatus database;
    private NLPStatus nlp;
    private LLMStatus llm;
    private Long timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatabaseStatus {
        private boolean connected;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NLPStatus {
        private boolean modelLoaded;
        private String modelVersion;
        private Integer totalIntents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LLMStatus {
        private boolean enabled;
        private String provider;
    }
}
