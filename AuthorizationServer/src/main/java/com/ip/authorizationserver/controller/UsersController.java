package com.ip.authorizationserver.controller;

import com.ip.authorizationserver.dto.CreateUserRequest;
import com.ip.authorizationserver.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        usersService.createUser(request.getUsername(), request.getName(), request.getEmail(), request.getPassword(), request.getRoles());
        return new ResponseEntity<>("User created!", HttpStatus.CREATED);
    }
}