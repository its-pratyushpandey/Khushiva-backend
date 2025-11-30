package com.chatbot.repository;

import com.chatbot.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Query("{ 'sessionId': ?0, 'createdAt': { $gte: ?1 } }")
    List<ChatMessage> findRecentMessages(String sessionId, LocalDateTime since);

    long countBySessionId(String sessionId);

    @Query("SELECT m.intent, COUNT(m) as count FROM ChatMessage m WHERE m.intent IS NOT NULL GROUP BY m.intent ORDER BY count DESC")
    List<Object[]> findMostCommonIntents();
}
