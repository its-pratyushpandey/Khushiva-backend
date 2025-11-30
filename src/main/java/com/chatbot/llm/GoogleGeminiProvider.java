package com.chatbot.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoogleGeminiProvider implements LLMProvider {

    @Value("${llm.google.api.key:}")
    private String apiKey;

    private final WebClient webClient;

    public GoogleGeminiProvider() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    @Override
    public String generateResponse(String prompt, String context) {
        if (!isAvailable()) {
            throw new IllegalStateException("Google Gemini API key not configured");
        }

        try {
            String fullPrompt = context != null 
                ? "Context: " + context + "\n\nUser: " + prompt 
                : prompt;

            Map<String, Object> request = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", "You are a helpful and friendly customer support assistant. Provide clear, concise, and accurate answers.")
                                    )
                            ),
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", fullPrompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "topK", 40,
                            "topP", 0.95,
                            "maxOutputTokens", 1024
                    )
            );

            log.info("Calling Google Gemini API with prompt: {}", fullPrompt);
            log.info("Request body: {}", request);

            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("Raw Gemini response: {}", response);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    if (content != null && content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty()) {
                            String text = (String) parts.get(0).get("text");
                            log.debug("Gemini response: {}", text);
                            return text;
                        }
                    }
                }
            }

            log.warn("No valid response from Gemini API");
            return null;
        } catch (Exception e) {
            log.error("Error calling Google Gemini API", e);
            return null;
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "Google Gemini";
    }
}
