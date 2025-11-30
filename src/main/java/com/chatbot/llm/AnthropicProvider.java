package com.chatbot.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnthropicProvider implements LLMProvider {

    @Value("${llm.anthropic.api.key:}")
    private String apiKey;

    private final WebClient webClient;

    public AnthropicProvider() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .build();
    }

    @Override
    public String generateResponse(String prompt, String context) {
        if (!isAvailable()) {
            throw new IllegalStateException("Anthropic API key not configured");
        }

        try {
            String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;

            Map<String, Object> request = Map.of(
                    "model", "claude-3-haiku-20240307",
                    "messages", List.of(
                            Map.of("role", "user", "content", fullPrompt)
                    ),
                    "max_tokens", 150
            );

            Map<String, Object> response = webClient.post()
                    .uri("/messages")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("content")) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
                if (!content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error calling Anthropic API", e);
            return null;
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "Anthropic";
    }
}
