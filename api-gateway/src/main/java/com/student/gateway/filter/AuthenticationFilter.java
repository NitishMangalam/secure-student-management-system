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

            // 1. Bypass logic for Auth requests (Login/Register)
            if (path.contains("/auth/register") || path.contains("/auth/login")) {
                return chain.filter(exchange);
            }

            // 2. Check if Authorization header exists
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = null;

            // 3. Extract Token correctly
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Header Format");
            }

            try {
                // 4. Validate Token (Using the fixed parseClaimsJws logic)
                jwtUtils.validateToken(token);

                // 5. EXTRACT the identity (The username inside the token)
                String username = jwtUtils.getUsernameFromToken(token);

                // 6. MUTATE the request to pass the username to downstream services
                // This is the "Identity Propagation" pattern
                return chain.filter(exchange.mutate()
                        .request(r -> r.header("X-Auth-User", username))
                        .build());

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or Expired Token");
            }
        };
    }
}