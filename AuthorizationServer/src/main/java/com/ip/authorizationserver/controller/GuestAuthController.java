package com.ip.authorizationserver.controller;

import com.ip.authorizationserver.service.GuestTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GuestAuthController {

    private final GuestTokenService guestTokenService;

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestToken() {
        String token = guestTokenService.generateGuestToken();
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "role", "GUEST"
        ));
    }
}