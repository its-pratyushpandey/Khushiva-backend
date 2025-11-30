package com.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQRequest {

    @NotBlank(message = "Intent is required")
    private String intent;

    @NotEmpty(message = "At least one pattern is required")
    private List<String> patterns;

    @NotEmpty(message = "At least one response is required")
    private List<String> responses;

    private String contextRequired;
    private List<String> quickReplies;
    private Integer priority;
    private Boolean isActive;
}
