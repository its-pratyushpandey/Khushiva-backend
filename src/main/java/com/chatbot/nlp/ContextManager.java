package com.chatbot.nlp;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContextManager {

    private final Map<String, SessionContext> sessionContexts = new ConcurrentHashMap<>();

    public void updateContext(String sessionId, String key, String value) {
        SessionContext context = sessionContexts.computeIfAbsent(sessionId, k -> new SessionContext());
        context.data.put(key, value);
        context.lastIntent = key;
    }

    public String getContext(String sessionId, String key) {
        SessionContext context = sessionContexts.get(sessionId);
        return context != null ? context.data.get(key) : null;
    }

    public String getLastIntent(String sessionId) {
        SessionContext context = sessionContexts.get(sessionId);
        return context != null ? context.lastIntent : null;
    }

    public void clearContext(String sessionId) {
        sessionContexts.remove(sessionId);
    }

    public Map<String, String> getAllContext(String sessionId) {
        SessionContext context = sessionContexts.get(sessionId);
        return context != null ? new HashMap<>(context.data) : new HashMap<>();
    }

    @Data
    private static class SessionContext {
        private Map<String, String> data = new HashMap<>();
        private String lastIntent;
    }
}
