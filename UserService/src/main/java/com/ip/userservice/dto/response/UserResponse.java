package com.ip.userservice.dto.response;

import com.ip.userservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String authProvider;
    private String id;
    private String fullName;
    private String email;
    private String bio;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Role role;
    private boolean isDarkMode;
}
