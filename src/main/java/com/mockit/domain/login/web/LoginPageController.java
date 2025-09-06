package com.mockit.domain.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {
    @GetMapping("/login")
    public String login() {
        // 시큐리티 커스텀 로그인 페이지 엔드포인트 → 카카오로 곧장 이동
        return "redirect:/oauth2/authorization/kakao";
    }
}