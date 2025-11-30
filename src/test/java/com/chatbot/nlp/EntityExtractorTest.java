package com.chatbot.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityExtractorTest {

    private EntityExtractor entityExtractor;

    @BeforeEach
    void setUp() {
        entityExtractor = new EntityExtractor();
    }

    @Test
    void testExtractEmail() {
        String text = "Contact me at john.doe@example.com for more info";
        List<IntentResult.Entity> entities = entityExtractor.extract(text);

        assertFalse(entities.isEmpty());
        assertTrue(entities.stream().anyMatch(e -> 
            e.getType().equals("email") && e.getValue().equals("john.doe@example.com")
        ));
    }

    @Test
    void testExtractPhone() {
        String text = "Call me at 555-123-4567";
        List<IntentResult.Entity> entities = entityExtractor.extract(text);

        assertFalse(entities.isEmpty());
        assertTrue(entities.stream().anyMatch(e -> e.getType().equals("phone")));
    }

    @Test
    void testExtractNumber() {
        String text = "I need 42 units";
        List<IntentResult.Entity> entities = entityExtractor.extract(text);

        assertFalse(entities.isEmpty());
        assertTrue(entities.stream().anyMatch(e -> 
            e.getType().equals("number") && e.getValue().equals("42")
        ));
    }

    @Test
    void testExtractPrice() {
        String text = "The price is $99.99";
        List<IntentResult.Entity> entities = entityExtractor.extract(text);

        assertFalse(entities.isEmpty());
        assertTrue(entities.stream().anyMatch(e -> 
            e.getType().equals("price") && e.getValue().contains("99.99")
        ));
    }

    @Test
    void testNoEntities() {
        String text = "Hello there";
        List<IntentResult.Entity> entities = entityExtractor.extract(text);

        assertTrue(entities.isEmpty());
    }
}
