package com.chatbot.controller;

import com.chatbot.dto.HealthResponse;
import com.chatbot.llm.LLMAdapter;
import com.chatbot.repository.FAQRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    private final MongoTemplate mongoTemplate;
    private final FAQRepository faqRepository;
    private final LLMAdapter llmAdapter;

    public HealthController(MongoTemplate mongoTemplate, FAQRepository faqRepository, LLMAdapter llmAdapter) {
        this.mongoTemplate = mongoTemplate;
        this.faqRepository = faqRepository;
        this.llmAdapter = llmAdapter;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check application health status")
    public ResponseEntity<HealthResponse> health() {
        boolean dbConnected = checkDatabase();
        long faqCount = faqRepository.count();

        HealthResponse.DatabaseStatus dbStatus = HealthResponse.DatabaseStatus.builder()
                .connected(dbConnected)
                .type("MongoDB")
                .build();

        HealthResponse.NLPStatus nlpStatus = HealthResponse.NLPStatus.builder()
                .modelLoaded(true)
                .modelVersion("1.0.0")
                .totalIntents((int) faqCount)
                .build();

        HealthResponse.LLMStatus llmStatus = HealthResponse.LLMStatus.builder()
                .enabled(llmAdapter.isLLMEnabled())
                .provider(llmAdapter.isLLMEnabled() ? "Google Gemini" : "None")
                .build();

        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .version("1.0.0")
                .database(dbStatus)
                .nlp(nlpStatus)
                .llm(llmStatus)
                .timestamp(System.currentTimeMillis())
                .build();

        return ResponseEntity.ok(response);
    }

    private boolean checkDatabase() {
        try {
            mongoTemplate.getDb().getName();
            return true;
        } catch (Exception e) {
            log.error("MongoDB health check failed", e);
            return false;
        }
    }
}
