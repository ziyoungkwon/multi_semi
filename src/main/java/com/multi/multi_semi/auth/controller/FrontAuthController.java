package com.multi.multi_semi.auth.controller;


import com.multi.multi_semi.auth.dto.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FrontAuthController {

    @GetMapping("/auth/signup")
    public String signup() {
        return "member/signup";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "common/login";
    }

    // OAuth2 성공 시 리디렉션될 콜백 페이지 반환
    // SecurityConfig에서 permitAll()한 /oauth-redirect 경로입니다.
    @GetMapping("/oauth-redirect")
    public String oauthCallbackPage() {
        return "common/oauth-callback";
    }

    // 만료된 엑세스토큰으로 서버 접근 시 header.html의 js가 정상작동 되는지 테스트
    @GetMapping("/refresh/test")
    public String refreshToken(@AuthenticationPrincipal CustomUser customUser) {
        return "main";
    }
}
