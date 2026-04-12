package com.student.auth_service.controller;

import com.student.auth_service.dto.AuthRequest;
import com.student.auth_service.entity.User;
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
        this.authService=authService;
    }
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.registerUser(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
