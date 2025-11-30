package com.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatbotApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }
}
