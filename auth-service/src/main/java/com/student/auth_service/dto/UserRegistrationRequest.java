package com.student.auth_service.dto;

public class UserRegistrationRequest {
    private String username;
    private String password;
    private String role; // Standardize this now (e.g., "STUDENT" or "ADMIN")

    public UserRegistrationRequest() {}

    public UserRegistrationRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}