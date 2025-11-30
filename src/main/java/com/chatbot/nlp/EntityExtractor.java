package com.chatbot.nlp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EntityExtractor {

    private static final List<EntityPattern> ENTITY_PATTERNS = List.of(
            new EntityPattern("email", Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")),
            new EntityPattern("phone", Pattern.compile("\\b(?:\\+?1[-.]?)?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})\\b")),
            new EntityPattern("url", Pattern.compile("\\bhttps?://[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+\\b")),
            new EntityPattern("number", Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b")),
            new EntityPattern("date", Pattern.compile("\\b(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\\s+\\d{1,2}(?:st|nd|rd|th)?(?:,?\\s+\\d{4})?\\b", Pattern.CASE_INSENSITIVE)),
            new EntityPattern("price", Pattern.compile("\\$\\s*\\d+(?:\\.\\d{2})?|\\d+(?:\\.\\d{2})?\\s*(?:dollars|usd)", Pattern.CASE_INSENSITIVE))
    );

    public List<IntentResult.Entity> extract(String text) {
        List<IntentResult.Entity> entities = new ArrayList<>();

        for (EntityPattern entityPattern : ENTITY_PATTERNS) {
            Matcher matcher = entityPattern.pattern.matcher(text);
            while (matcher.find()) {
                entities.add(IntentResult.Entity.builder()
                        .type(entityPattern.type)
                        .value(matcher.group())
                        .start(matcher.start())
                        .end(matcher.end())
                        .build());
            }
        }

        return entities;
    }

    private static class EntityPattern {
        String type;
        Pattern pattern;

        EntityPattern(String type, Pattern pattern) {
            this.type = type;
            this.pattern = pattern;
        }
    }
}
