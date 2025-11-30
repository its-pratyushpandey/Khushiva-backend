package com.chatbot.repository;

import com.chatbot.entity.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

    Optional<ChatSession> findByIdAndIsActiveTrue(String id);

    List<ChatSession> findByUserIdentifierOrderByCreatedAtDesc(String userIdentifier);

    @Query("{ 'isActive': true, 'lastActivityAt': { $lt: ?0 } }")
    List<ChatSession> findInactiveSessions(LocalDateTime threshold);

    long countByUserIdentifierAndIsActiveTrue(String userIdentifier);
}
