package com.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("full_name")
    private String fullName;

    @Indexed(unique = true, sparse = true)
    @Field("phone")
    private String phone;

    @Field("phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @Field("email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Field("auth_provider")
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Field("provider_id")
    private String providerId;

    @Field("profile_picture")
    private String profilePicture;

    @Field("roles")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Field("last_login")
    private LocalDateTime lastLogin;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public enum AuthProvider {
        LOCAL, GOOGLE, GITHUB, MICROSOFT
    }
}
