package com.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "model_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelMetadata {

    @Id
    private String id;

    @Field("model_name")
    private String modelName;

    @Field("model_version")
    private String modelVersion;

    @Field("model_path")
    private String modelPath;

    @Field("accuracy")
    private Double accuracy;

    @Field("training_samples")
    private Integer trainingSamples;

    @Field("is_active")
    @Builder.Default
    private Boolean isActive = false;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
