package com.student.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtils {

    // MUST match the Secret Key in Auth-Service exactly
    private static final String SECRET_KEY = "your_very_secret_key_make_it_long_and_secure_2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 1. Fixed validation logic
    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    // 2. Added the missing method to extract the username
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}