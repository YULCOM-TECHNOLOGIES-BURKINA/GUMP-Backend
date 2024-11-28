package com.yulcomtechnologies.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Component
public class UserExtractionFilter implements GlobalFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Extract token from the Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        System.out.println("authHeader: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Decode token without validation
            try {
                String[] tokenParts = token.split("\\.");
                if (tokenParts.length >= 2) {
                    String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
                    Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
                    System.out.println(claims);

                    // Extract claims (adjust keys based on your token structure)
                    String userId = claims.get("sub").toString(); // Subject (user ID)
                    String roles = claims.get("realm_access") != null
                        ? claims.get("realm_access").toString()
                        : null;

                    // Add user data as headers to the downstream request
                    ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", roles != null ? roles : "N/A")
                        .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }
            } catch (Exception e) {
                // Log error or handle invalid token
                System.err.println("Error decoding token: " + e.getMessage());
            }
        }
        return chain.filter(exchange);
    }
}
