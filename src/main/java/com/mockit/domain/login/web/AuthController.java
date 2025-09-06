package com.mockit.domain.login.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 앱/웹 공통 진입점(테스트용). 실제 앱은 바로 /oauth2/authorization/kakao 로 열어도 됩니다.
    @GetMapping("/login")
    public String loginEntry() {
        return "<a href=\"/oauth2/authorization/kakao\">카카오로 로그인</a>";
    }

    // 로그인 상태/프로필 확인용
    @GetMapping("/me")
    public Object me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return Map.of("authenticated", false);

        var kakaoAccount = (Map<?, ?>) principal.getAttribute("kakao_account");
        var profile = kakaoAccount != null ? (Map<?, ?>) kakaoAccount.get("profile") : null;

        return Map.of(
                "authenticated", true,
                "memberId", principal.getAttribute("memberId"),
                "kakaoId", principal.getAttribute("id"),
//                "email", kakaoAccount != null ? kakaoAccount.get("email") : null,
                "nickname", profile != null ? profile.get("nickname") : null
        );
    }
}