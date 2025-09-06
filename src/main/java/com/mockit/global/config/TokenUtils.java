package com.mockit.global.config;

import com.mockit.domain.login.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenUtils {

    // Inject your JwtUtils class to use its parsing methods
    private final JwtUtils jwtUtils;

    /**
     * Extracts the member ID from a JWT token.
     * @param token The JWT token string.
     * @return The extracted member ID.
     */
    public Long getMemberIdFromToken(String token) {
        // Use the parsing logic from JwtUtils to get the claims
        Claims claims = jwtUtils.parseClaims(token);

        // Extract the memberId from the subject claim
        // This assumes the subject is set to String.valueOf(memberId) when the token is created.
        return Long.valueOf(claims.getSubject());
    }
}