package com.chatbot.controller;

import com.chatbot.dto.TrainRequest;
import com.chatbot.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/train")
@Tag(name = "Training", description = "Model training endpoints")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    @Operation(summary = "Train model", description = "Trigger model training from FAQ dataset")
    public ResponseEntity<Map<String, Object>> train(@RequestBody(required = false) TrainRequest request) {
        log.info("Training request received");
        if (request == null) {
            request = new TrainRequest();
        }
        Map<String, Object> result = trainingService.trainFromDataset(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/model-info")
    @Operation(summary = "Get model info", description = "Get current model information")
    public ResponseEntity<Map<String, Object>> getModelInfo() {
        Map<String, Object> info = trainingService.getModelInfo();
        return ResponseEntity.ok(info);
    }
}
