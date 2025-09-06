package com.mockit.domain.login.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties props;

    private Key key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // ✅ email 포함 버전 (기존)
    public String createAccessToken(Long memberId, String email, String role) {
        return createToken(memberId, email, role, props.getAccessTokenExpiration());
    }

    public String createRefreshToken(Long memberId, String email, String role) {
        return createToken(memberId, email, role, props.getRefreshTokenExpiration());
    }

    // ✅ email 없이 발급하는 버전 (새로 추가)
    public String createAccessToken(Long memberId, String role) {
        return createToken(memberId, null, role, props.getAccessTokenExpiration());
    }

    public String createRefreshToken(Long memberId, String role) {
        return createToken(memberId, null, role, props.getRefreshTokenExpiration());
    }

    // 내부 공통 로직
    private String createToken(Long memberId, String email, String role, long ttlMs) {
        Date now = new Date();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        if (email != null) {  // ✅ email이 있을 때만 claim 추가
            claims.put("email", email);
        }

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttlMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}