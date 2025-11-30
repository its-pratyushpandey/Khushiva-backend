package com.chatbot.repository;

import com.chatbot.entity.FAQ;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FAQRepository extends MongoRepository<FAQ, String> {

    Optional<FAQ> findByIntent(String intent);

    List<FAQ> findByIsActiveTrueOrderByPriorityDesc();

    List<FAQ> findAllByOrderByPriorityDesc();

    boolean existsByIntent(String intent);
}
