package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String id;
    private String content;
    private String senderType;
    private String intent;
    private Double confidenceScore;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
