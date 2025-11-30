package com.chatbot.service;

import com.chatbot.entity.OTP;
import com.chatbot.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPRepository otpRepository;
    private final Random random = new Random();

    @Transactional
    public String generateAndSendOTP(String phone) {
        // Delete any existing OTPs for this phone
        otpRepository.deleteByPhone(phone);

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", random.nextInt(1000000));

        // Save OTP to database
        OTP otp = OTP.builder()
                .phone(phone)
                .otpCode(otpCode)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(otp);

        // In production, integrate with SMS service (Twilio, AWS SNS, etc.)
        log.info("OTP for {}: {}", phone, otpCode);
        
        // For development, return OTP (remove in production)
        return otpCode;
    }

    @Transactional
    public boolean verifyOTP(String phone, String otpCode) {
        OTP otp = otpRepository.findByPhoneAndVerifiedFalse(phone)
                .orElse(null);

        if (otp == null) {
            log.warn("No OTP found for phone: {}", phone);
            return false;
        }

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP expired for phone: {}", phone);
            otpRepository.delete(otp);
            return false;
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            log.warn("Invalid OTP for phone: {}", phone);
            return false;
        }

        // Mark as verified
        otp.setVerified(true);
        otpRepository.save(otp);

        return true;
    }
}
