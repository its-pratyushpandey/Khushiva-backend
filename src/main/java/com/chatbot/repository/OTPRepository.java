package com.chatbot.repository;

import com.chatbot.entity.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends MongoRepository<OTP, String> {

    Optional<OTP> findByPhoneAndVerifiedFalse(String phone);

    void deleteByPhone(String phone);
}
