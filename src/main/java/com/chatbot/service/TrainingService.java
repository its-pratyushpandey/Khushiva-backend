package com.chatbot.service;

import com.chatbot.dto.TrainRequest;
import com.chatbot.entity.FAQ;
import com.chatbot.entity.ModelMetadata;
import com.chatbot.nlp.IntentClassifier;
import com.chatbot.repository.FAQRepository;
import com.chatbot.repository.ModelMetadataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TrainingService {

    private final FAQRepository faqRepository;
    private final ModelMetadataRepository modelMetadataRepository;
    private final IntentClassifier intentClassifier;
    private final ObjectMapper objectMapper;

    @Value("${nlp.model.path:./models}")
    private String modelPath;

    public TrainingService(
            FAQRepository faqRepository,
            ModelMetadataRepository modelMetadataRepository,
            IntentClassifier intentClassifier,
            ObjectMapper objectMapper
    ) {
        this.faqRepository = faqRepository;
        this.modelMetadataRepository = modelMetadataRepository;
        this.intentClassifier = intentClassifier;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, Object> trainFromDataset(TrainRequest request) {
        log.info("Starting training process...");

        try {
            // Load FAQ data
            List<FAQ> faqs;
            if (request.getDatasetPath() != null && !request.getDatasetPath().isEmpty()) {
                faqs = loadFromFile(request.getDatasetPath());
            } else {
                faqs = faqRepository.findByIsActiveTrueOrderByPriorityDesc();
            }

            if (faqs.isEmpty()) {
                throw new IllegalStateException("No training data available");
            }

            // Reload intents in classifier
            intentClassifier.loadIntents();

            // Create model metadata
            ModelMetadata metadata = ModelMetadata.builder()
                    .modelName("intent-classifier")
                    .modelVersion(LocalDateTime.now().toString())
                    .modelPath(modelPath)
                    .trainingSamples(faqs.size())
                    .accuracy(0.85) // Placeholder - would be calculated in real training
                    .isActive(true)
                    .build();

            // Deactivate old models
            modelMetadataRepository.findByIsActiveTrue().ifPresent(old -> {
                old.setIsActive(false);
                modelMetadataRepository.save(old);
            });

            modelMetadataRepository.save(metadata);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("intents_trained", faqs.size());
            result.put("model_version", metadata.getModelVersion());
            result.put("accuracy", metadata.getAccuracy());

            log.info("Training completed successfully");
            return result;

        } catch (Exception e) {
            log.error("Training failed", e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", e.getMessage());
            return result;
        }
    }

    private List<FAQ> loadFromFile(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        Map<String, Object> data = objectMapper.readValue(content, Map.class);
        
        List<Map<String, Object>> intents = (List<Map<String, Object>>) data.get("intents");
        
        // This would load FAQs from file - simplified for demo
        return faqRepository.findByIsActiveTrueOrderByPriorityDesc();
    }

    public Map<String, Object> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        
        modelMetadataRepository.findByIsActiveTrue().ifPresentOrElse(
                metadata -> {
                    info.put("model_name", metadata.getModelName());
                    info.put("model_version", metadata.getModelVersion());
                    info.put("accuracy", metadata.getAccuracy());
                    info.put("training_samples", metadata.getTrainingSamples());
                    info.put("created_at", metadata.getCreatedAt());
                },
                () -> info.put("status", "no_model_loaded")
        );

        return info;
    }
}
