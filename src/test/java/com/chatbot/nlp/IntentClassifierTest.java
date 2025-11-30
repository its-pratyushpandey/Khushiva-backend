package com.chatbot.nlp;

import com.chatbot.entity.FAQ;
import com.chatbot.repository.FAQRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntentClassifierTest {

    @Mock
    private FAQRepository faqRepository;

    private IntentClassifier intentClassifier;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        intentClassifier = new IntentClassifier(faqRepository, objectMapper);

        // Mock FAQ data
        FAQ greetingFAQ = FAQ.builder()
                .intent("greeting")
                .patterns(objectMapper.writeValueAsString(Arrays.asList(
                        "^(hi|hello|hey).*",
                        ".*\\b(hi|hello)\\b.*"
                )))
                .responses(objectMapper.writeValueAsString(Arrays.asList("Hello!", "Hi there!")))
                .priority(100)
                .isActive(true)
                .build();

        FAQ pricingFAQ = FAQ.builder()
                .intent("pricing")
                .patterns(objectMapper.writeValueAsString(Arrays.asList(
                        ".*\\b(price|pricing|cost)\\b.*"
                )))
                .responses(objectMapper.writeValueAsString(Arrays.asList("Our pricing starts at $29/month")))
                .priority(80)
                .isActive(true)
                .build();

        when(faqRepository.findByIsActiveTrueOrderByPriorityDesc())
                .thenReturn(Arrays.asList(greetingFAQ, pricingFAQ));

        intentClassifier.initialize();
    }

    @Test
    void testClassifyGreeting() {
        IntentResult result = intentClassifier.classify("Hello there!");
        
        assertNotNull(result);
        assertEquals("greeting", result.getIntent());
        assertTrue(result.getConfidence() > 0.6);
    }

    @Test
    void testClassifyPricing() {
        IntentResult result = intentClassifier.classify("What is the pricing?");
        
        assertNotNull(result);
        assertEquals("pricing", result.getIntent());
        assertTrue(result.getConfidence() > 0.6);
    }

    @Test
    void testClassifyUnknown() {
        IntentResult result = intentClassifier.classify("xyzabc random text");
        
        assertNotNull(result);
        assertEquals("unknown", result.getIntent());
        assertEquals(0.0, result.getConfidence());
    }

    @Test
    void testMeetsThreshold() {
        assertTrue(intentClassifier.meetsThreshold(0.70));
        assertFalse(intentClassifier.meetsThreshold(0.60));
    }
}
