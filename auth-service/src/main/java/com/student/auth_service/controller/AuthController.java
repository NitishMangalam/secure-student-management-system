package com.student.auth_service.controller;

import com.student.auth_service.dto.AuthRequest;
import com.student.auth_service.dto.UserRegistrationRequest; // Added this import
import com.student.auth_service.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    // 1. Changed parameter type from 'User' to 'UserRegistrationRequest'
    // 2. Changed parameter name from 'user' to 'request' to match the return line
    public String register(@RequestBody UserRegistrationRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}