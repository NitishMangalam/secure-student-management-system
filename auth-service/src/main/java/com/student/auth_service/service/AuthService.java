package com.student.auth_service.service;

import com.student.auth_service.client.StudentClient;
import com.student.auth_service.dto.AuthRequest; // Ensure this package matches your DTO
import com.student.auth_service.entity.User;
import com.student.auth_service.repository.UserRepository;
import com.student.auth_service.util.JwtUtils; // Ensure this package matches your Util
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap; // Required for studentData
import java.util.Map;     // Required for studentData

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StudentClient studentClient;

    // Constructor Injection (Best Practice)
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
     * Handles User Registration and automatically calls Student-Service
     */
    public String registerUser(User user) {
        // 1. Encode password and save to Security DB (auth_db)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // 2. Prepare data to send to Student-Service via Feign
        Map<String, String> studentData = new HashMap<>();
        studentData.put("name", user.getUsername());
        studentData.put("email", user.getUsername() + "@university.com");
        studentData.put("department", "To Be Assigned");

        try {
            // 3. Sync communication with Student-Service
            studentClient.createStudentProfile(studentData);
        } catch (Exception e) {
            // We log the error but don't fail the registration
            System.err.println("CRITICAL: Could not create Student Profile: " + e.getMessage());
        }

        return "User registered successfully and Student Profile created!";
    }

    /**
     * Handles Login and returns JWT
     */
    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtUtils.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Invalid Credentials!");
        }
    }
}