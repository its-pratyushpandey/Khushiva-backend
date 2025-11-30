package com.chatbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI chatbotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Chatbot API")
                        .description("Production-ready AI Chatbot with NLP, WebSocket, and optional LLM integration")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Chatbot Team")
                                .email("support@chatbot.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.chatbot.com").description("Production Server")
                ));
    }
}
