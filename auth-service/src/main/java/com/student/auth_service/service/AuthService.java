package com.student.auth_service.service;

import com.student.auth_service.client.StudentClient;
import com.student.auth_service.dto.AuthRequest;
import com.student.auth_service.dto.UserRegistrationRequest;
import com.student.auth_service.entity.User;
import com.student.auth_service.repository.UserRepository;
import com.student.auth_service.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StudentClient studentClient;

    // Constructor Injection
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       StudentClient studentClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.studentClient = studentClient;
    }

    /**
     * Handles User Registration and calls Student-Service via Feign
     */
    public String registerUser(UserRegistrationRequest request) {
        // 1. Safety Check: Does the user already exist?
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Error: Username is already taken!";
        }

        // 2. Map DTO to Database Entity
        User user = new User();
        user.setUsername(request.getUsername());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Set default role if not provided
        user.setRole(request.getRole() != null ? request.getRole() : "STUDENT");

        userRepository.save(user);

        // 3. Synchronous Feign call to Student Service
        try {
            Map<String, String> studentData = new HashMap<>();
            studentData.put("name", user.getUsername());
            studentData.put("email", user.getUsername() + "@university.com");
            studentData.put("department", "To Be Assigned");

            studentClient.createStudentProfile(studentData);
        } catch (Exception e) {
            // Log the error but allow registration to succeed
            System.err.println("CRITICAL: Could not create Student Profile: " + e.getMessage());
        }

        return "User registered successfully!";
    }

    /**
     * Handles Login and returns a JWT
     */
    public String login(AuthRequest request) {
        // Find user or throw error
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if plain password matches hashed password in DB
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtUtils.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Invalid Credentials!");
        }
    }
}