package com.student.gateway.filter;

import com.student.gateway.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtils jwtUtils;

    public AuthenticationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // 1. BYPASS logic for Auth requests
            if (path.contains("/auth/register") || path.contains("/auth/login")) {
                return chain.filter(exchange);
            }

            // 2. Check if Authorization header exists
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                // Return 401 instead of crashing with 500
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            // 3. Extract Token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Header Format");
            }

            try {
                // 4. Validate Token
                jwtUtils.validateToken(authHeader);
            } catch (Exception e) {
                // Return 401 instead of crashing with 500
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or Expired Token");
            }

            return chain.filter(exchange);
        };
    }
}