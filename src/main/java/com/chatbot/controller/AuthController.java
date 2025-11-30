package com.chatbot.controller;

import com.chatbot.dto.*;
import com.chatbot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/phone/send-otp")
    public ResponseEntity<ApiResponse> sendOTP(@Valid @RequestBody PhoneLoginRequest request) {
        log.info("OTP request for phone: {}", request.getPhone());
        try {
            ApiResponse response = authService.sendOTP(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Send OTP failed: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/phone/verify-otp")
    public ResponseEntity<AuthResponse> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        log.info("OTP verification for phone: {}", request.getPhone());
        try {
            AuthResponse response = authService.verifyOTPAndLogin(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("OTP verification failed: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkAuth() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Authentication service is running")
                .build());
    }
}
