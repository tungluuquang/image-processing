package com.ip.userservice.controller;


import com.ip.userservice.dto.request.UserRequest;
import com.ip.userservice.dto.response.UserResponse;
import com.ip.userservice.model.Role;
import com.ip.userservice.service.UserService;
import com.ip.userservice.validation.OnCreate;
import com.ip.userservice.validation.OnUpdate;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUserResponseById(authentication.getName()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Validated(OnCreate.class) @RequestBody UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Role role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> Role.valueOf(grantedAuthority.getAuthority().replace("ROLE_", "")))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No role found for user"));
        return new ResponseEntity<>(userService.create(authentication.getName(), role, userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> update(
            @PathVariable String userId,
            @Validated(OnUpdate.class) @RequestBody UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(userService.update(authentication.getName(), userRequest), HttpStatus.OK);
    }

}
