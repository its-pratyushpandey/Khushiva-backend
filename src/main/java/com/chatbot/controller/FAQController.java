package com.chatbot.controller;

import com.chatbot.dto.FAQRequest;
import com.chatbot.entity.FAQ;
import com.chatbot.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/faq")
@Tag(name = "FAQ", description = "FAQ management endpoints")
public class FAQController {

    private final FAQService faqService;

    public FAQController(FAQService faqService) {
        this.faqService = faqService;
    }

    @PostMapping
    @Operation(summary = "Create or update FAQ", description = "Add or update an FAQ entry")
    public ResponseEntity<FAQ> createOrUpdateFAQ(@Valid @RequestBody FAQRequest request) {
        log.info("Creating/updating FAQ for intent: {}", request.getIntent());
        FAQ faq = faqService.createOrUpdateFAQ(request);
        return ResponseEntity.ok(faq);
    }

    @GetMapping
    @Operation(summary = "Get all FAQs", description = "Retrieve all FAQ entries")
    public ResponseEntity<List<FAQ>> getAllFAQs() {
        List<FAQ> faqs = faqService.getAllFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/{intent}")
    @Operation(summary = "Get FAQ by intent", description = "Retrieve a specific FAQ by intent")
    public ResponseEntity<FAQ> getFAQByIntent(@PathVariable String intent) {
        FAQ faq = faqService.getFAQByIntent(intent);
        return ResponseEntity.ok(faq);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete FAQ", description = "Delete an FAQ entry")
    public ResponseEntity<Void> deleteFAQ(@PathVariable String id) {
        log.info("Deleting FAQ: {}", id);
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }
}
