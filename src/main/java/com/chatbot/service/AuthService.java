package com.chatbot.service;

import com.chatbot.dto.*;
import com.chatbot.entity.User;
import com.chatbot.repository.UserRepository;
import com.chatbot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final OTPService otpService;

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if phone already exists (if provided)
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create new user
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(roles)
                .authProvider(User.AuthProvider.LOCAL)
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();

        userRepository.save(user);

        // Authenticate and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePicture(user.getProfilePicture())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    public AuthResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        // Update last login
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePicture(user.getProfilePicture())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    public ApiResponse sendOTP(PhoneLoginRequest request) {
        String otpCode = otpService.generateAndSendOTP(request.getPhone());
        
        // In development, return OTP in response (remove in production)
        return ApiResponse.builder()
                .success(true)
                .message("OTP sent successfully. OTP: " + otpCode)
                .build();
    }

    @Transactional
    public AuthResponse verifyOTPAndLogin(VerifyOTPRequest request) {
        // Verify OTP
        boolean isValid = otpService.verifyOTP(request.getPhone(), request.getOtp());
        
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Find or create user
        User user = userRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    Set<String> roles = new HashSet<>();
                    roles.add("ROLE_USER");
                    
                    User newUser = User.builder()
                            .phone(request.getPhone())
                            .authProvider(User.AuthProvider.LOCAL)
                            .phoneVerified(true)
                            .roles(roles)
                            .isActive(true)
                            .fullName("User " + request.getPhone().substring(Math.max(0, request.getPhone().length() - 4)))
                            .build();
                    
                    return userRepository.save(newUser);
                });

        // Mark phone as verified
        user.setPhoneVerified(true);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateTokenFromEmail(user.getEmail() != null ? user.getEmail() : user.getPhone());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePicture(user.getProfilePicture())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Transactional
    public AuthResponse processOAuthLogin(String email, String fullName, String profilePicture, User.AuthProvider provider, String providerId) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Set<String> roles = new HashSet<>();
                    roles.add("ROLE_USER");
                    
                    User newUser = User.builder()
                            .email(email)
                            .fullName(fullName)
                            .profilePicture(profilePicture)
                            .authProvider(provider)
                            .providerId(providerId)
                            .emailVerified(true)
                            .roles(roles)
                            .isActive(true)
                            .build();
                    
                    return userRepository.save(newUser);
                });

        // Update profile picture if changed
        if (profilePicture != null && !profilePicture.equals(user.getProfilePicture())) {
            user.setProfilePicture(profilePicture);
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateTokenFromEmail(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePicture(user.getProfilePicture())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }
}
