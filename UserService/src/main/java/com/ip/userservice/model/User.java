package com.ip.userservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(name = "auth_provider", nullable = false)
    private String authProvider;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

//    @Builder.Default
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Builder.Default
    @Column(name = "is_dark_mode", nullable = false, columnDefinition = "boolean default false")
    private boolean isDarkMode = false;

    @Enumerated(EnumType.STRING)
    private Role role;
}
