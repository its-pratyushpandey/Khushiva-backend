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
public class OpenAIProvider implements LLMProvider {

    @Value("${llm.openai.api.key:}")
    private String apiKey;

    private final WebClient webClient;

    public OpenAIProvider() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    @Override
    public String generateResponse(String prompt, String context) {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API key not configured");
        }

        try {
            String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;

            Map<String, Object> request = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a helpful customer support assistant."),
                            Map.of("role", "user", "content", fullPrompt)
                    ),
                    "temperature", 0.7,
                    "max_tokens", 150
            );

            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return null;
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }
}
