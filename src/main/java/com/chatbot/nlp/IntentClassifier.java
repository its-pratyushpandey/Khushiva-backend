package com.chatbot.nlp;

import com.chatbot.entity.FAQ;
import com.chatbot.repository.FAQRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IntentClassifier {

    private final FAQRepository faqRepository;
    private final ObjectMapper objectMapper;

    @Value("${nlp.confidence.threshold:0.65}")
    private double confidenceThreshold;

    private Map<String, List<Pattern>> intentPatterns;
    private Map<String, Integer> intentPriority;

    public IntentClassifier(FAQRepository faqRepository, ObjectMapper objectMapper) {
        this.faqRepository = faqRepository;
        this.objectMapper = objectMapper;
        this.intentPatterns = new HashMap<>();
        this.intentPriority = new HashMap<>();
    }

    @PostConstruct
    public void initialize() {
        loadIntents();
    }

    public void loadIntents() {
        log.info("Loading intents from database...");
        List<FAQ> faqs = faqRepository.findByIsActiveTrueOrderByPriorityDesc();
        
        intentPatterns.clear();
        intentPriority.clear();

        for (FAQ faq : faqs) {
            try {
                List<String> patterns = objectMapper.readValue(faq.getPatterns(), List.class);
                List<Pattern> compiledPatterns = patterns.stream()
                        .map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
                        .collect(Collectors.toList());
                
                intentPatterns.put(faq.getIntent(), compiledPatterns);
                intentPriority.put(faq.getIntent(), faq.getPriority());
            } catch (Exception e) {
                log.error("Error loading patterns for intent: {}", faq.getIntent(), e);
            }
        }

        log.info("Loaded {} intents", intentPatterns.size());
    }

    public IntentResult classify(String text) {
        String normalizedText = normalize(text);
        
        // Rule-based pattern matching
        List<IntentMatch> matches = new ArrayList<>();
        
        for (Map.Entry<String, List<Pattern>> entry : intentPatterns.entrySet()) {
            String intent = entry.getKey();
            List<Pattern> patterns = entry.getValue();
            
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(normalizedText);
                if (matcher.find()) {
                    double confidence = calculateConfidence(normalizedText, pattern);
                    int priority = intentPriority.getOrDefault(intent, 0);
                    matches.add(new IntentMatch(intent, confidence, priority));
                    break; // Found a match for this intent
                }
            }
        }

        if (matches.isEmpty()) {
            return IntentResult.builder()
                    .intent("unknown")
                    .confidence(0.0)
                    .normalizedText(normalizedText)
                    .entities(new ArrayList<>())
                    .slots(new HashMap<>())
                    .build();
        }

        // Sort by confidence and priority
        matches.sort((a, b) -> {
            int confCompare = Double.compare(b.confidence, a.confidence);
            if (confCompare != 0) return confCompare;
            return Integer.compare(b.priority, a.priority);
        });

        IntentMatch bestMatch = matches.get(0);

        return IntentResult.builder()
                .intent(bestMatch.intent)
                .confidence(bestMatch.confidence)
                .normalizedText(normalizedText)
                .entities(new ArrayList<>())
                .slots(new HashMap<>())
                .build();
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private double calculateConfidence(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String matchedText = matcher.group();
            double matchRatio = (double) matchedText.length() / text.length();
            return Math.min(0.95, 0.7 + (matchRatio * 0.25));
        }
        return 0.0;
    }

    public boolean meetsThreshold(double confidence) {
        return confidence >= confidenceThreshold;
    }

    private static class IntentMatch {
        String intent;
        double confidence;
        int priority;

        IntentMatch(String intent, double confidence, int priority) {
            this.intent = intent;
            this.confidence = confidence;
            this.priority = priority;
        }
    }
}
