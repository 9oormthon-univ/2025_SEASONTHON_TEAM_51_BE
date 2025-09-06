package com.mockit.global.config;

import org.springframework.stereotype.Component;

@Component
public class TokenUtils {

    /**
     * Extracts the member ID from a JWT token.
     * Note: This is a placeholder. You need to implement the actual JWT parsing logic.
     * @param token The JWT token string.
     * @return The extracted member ID.
     */
    public Long getMemberIdFromToken(String token) {
        // This is a dummy implementation for testing.
        // In a real application, you would parse the token and validate its claims.
        // Example with JJWT library:
        // Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("memberId", Long.class);

        // Return a dummy ID for now so your application can start.
        return 1L;
    }
}