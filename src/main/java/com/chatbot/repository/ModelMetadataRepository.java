package com.chatbot.repository;

import com.chatbot.entity.ModelMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelMetadataRepository extends MongoRepository<ModelMetadata, String> {

    Optional<ModelMetadata> findByIsActiveTrue();

    Optional<ModelMetadata> findByModelNameAndModelVersion(String modelName, String modelVersion);
}
