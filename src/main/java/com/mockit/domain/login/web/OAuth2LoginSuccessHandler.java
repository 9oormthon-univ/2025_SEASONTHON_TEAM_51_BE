package com.mockit.domain.login.web;

import com.mockit.domain.login.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Authentication authentication
    ) {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // KakaoOAuth2UserService에서 attr로 세팅해둔 memberId 사용
        Long memberId = principal.getAttribute("memberId");

        // ✅ email 제거 → memberId와 role만으로 토큰 생성
        String access = jwtUtils.createAccessToken(memberId, "ROLE_USER");
        String refresh = jwtUtils.createRefreshToken(memberId, "ROLE_USER");

        addCookie(response, "ACCESS_TOKEN", access, 60 * 60);            // 1h
        addCookie(response, "REFRESH_TOKEN", refresh, 7 * 24 * 60 * 60); // 7d

        try {
            response.sendRedirect("/");
        } catch (Exception ignored) {}
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSec);
        // 운영 HTTPS 환경에서는 반드시 활성화
        // cookie.setSecure(true);
        response.addCookie(cookie);
    }
}