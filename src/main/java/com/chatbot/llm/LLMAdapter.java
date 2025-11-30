package com.chatbot.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LLMAdapter {

    @Value("${llm.provider:}")
    private String providerName;

    private final OpenAIProvider openAIProvider;
    private final AnthropicProvider anthropicProvider;
    private final GoogleGeminiProvider googleGeminiProvider;

    public LLMAdapter(OpenAIProvider openAIProvider, AnthropicProvider anthropicProvider, GoogleGeminiProvider googleGeminiProvider) {
        this.openAIProvider = openAIProvider;
        this.anthropicProvider = anthropicProvider;
        this.googleGeminiProvider = googleGeminiProvider;
    }

    public String getResponse(String prompt, String context) {
        LLMProvider provider = getActiveProvider();

        if (provider == null || !provider.isAvailable()) {
            log.debug("No LLM provider available, returning null");
            return null;
        }

        try {
            log.info("Using {} for LLM response", provider.getProviderName());
            return provider.generateResponse(prompt, context);
        } catch (Exception e) {
            log.error("Error getting LLM response from {}", provider.getProviderName(), e);
            return null;
        }
    }

    public boolean isLLMEnabled() {
        LLMProvider provider = getActiveProvider();
        return provider != null && provider.isAvailable();
    }

    private LLMProvider getActiveProvider() {
        if (providerName == null || providerName.trim().isEmpty()) {
            return null;
        }

        return switch (providerName.toLowerCase()) {
            case "openai" -> openAIProvider;
            case "anthropic" -> anthropicProvider;
            case "google", "gemini" -> googleGeminiProvider;
            default -> {
                log.warn("Unknown LLM provider: {}", providerName);
                yield null;
            }
        };
    }
}
