package com.chatbot.nlp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResult {

    private String intent;
    private double confidence;
    private List<Entity> entities;
    private Map<String, String> slots;
    private String normalizedText;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private String type;
        private String value;
        private int start;
        private int end;
    }
}
