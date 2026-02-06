package com.ip.userservice.service;

import com.ip.userservice.dto.request.UserRequest;
import com.ip.userservice.dto.response.UserResponse;
import com.ip.userservice.mapper.UserMapper;
import com.ip.userservice.model.Role;
import com.ip.userservice.model.User;
import com.ip.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserResponseById(String id) {
        UserResponse user = findById(id);
        return user;
    }

    public UserResponse findById(String id) {
        return userMapper.toResponse(this.findEntityById(id));
    }

    @Transactional
    public UserResponse create(String userId, Role role, UserRequest userRequest) {
        User user = User.builder()
                .id(userId)
                .email(userRequest.getEmail())
                .authProvider(userRequest.getAuthProvider())
                .bio(userRequest.getBio())
                .avatarUrl(userRequest.getAvatarUrl())
                .username(userRequest.getUsername())
                .isDarkMode(userRequest.isDarkMode())
                .fullName(userRequest.getFullName())
                .phoneNumber(userRequest.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @PreAuthorize("authentication.name == #userId")
    public UserResponse update(String userId, UserRequest userRequest) throws AccessDeniedException {
        User existedUser = this.findEntityById(userId);
        userMapper.updateUserFromRequest(userRequest, existedUser);
        return userMapper.toResponse(userRepository.save(existedUser));
    }

    public User findEntityById(String id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such user with id " + id));
    }
}
