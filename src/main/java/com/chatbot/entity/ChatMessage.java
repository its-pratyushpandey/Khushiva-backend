package com.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    private String id;

    @Field("session_id")
    private String sessionId;

    @Field("content")
    private String content;

    @Field("sender_type")
    private SenderType senderType;

    @Field("intent")
    private String intent;

    @Field("confidence_score")
    private Double confidenceScore;

    @Field("entities")
    private String entities;

    @Field("is_read")
    @Builder.Default
    private Boolean isRead = false;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    public enum SenderType {
        USER, BOT, SYSTEM
    }
}
