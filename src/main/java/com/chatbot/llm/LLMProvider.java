package com.chatbot.llm;

public interface LLMProvider {

    String generateResponse(String prompt, String context);

    boolean isAvailable();

    String getProviderName();
}
