package com.multi.multi_semi.auth.service;


import com.multi.multi_semi.auth.dto.CustomOAuth2UserWrapper;
import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.jwt.TokenProvider;
import com.multi.multi_semi.common.jwt.dto.TokenDto;
import com.multi.multi_semi.common.jwt.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {

        // 1. 인증된 사용자 정보 가져오기 (CustomOAuth2UserWrapper)
        CustomOAuth2UserWrapper oAuth2User = (CustomOAuth2UserWrapper) authentication.getPrincipal();
        CustomUser customUser = oAuth2User.getCustomUser();

        String email = customUser.getUsername(); // getUsername()이 email을 반환
        List<String> roles = customUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("OAuth2 로그인 성공. 이메일: {}, 권한: {}", email, roles);

        // 2. 님의 AuthService.login()과 동일하게 TokenService 호출 준비
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("roles", roles);

        // 3. 님의 TokenService를 호출하여 JWT(AT+RT) 발급
        TokenDto tokenDto = tokenService.createTokenForLogin(loginData);

        // 4. RT는 HttpOnly 쿠키로 설정 (AuthController 로직과 100% 동일)
        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .maxAge(tokenProvider.getRefreshTokenExpirySeconds())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, rtCookie.toString());

        // 5. AT는 프론트엔드 URL의 쿼리 파라미터로 전송
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8090/oauth-redirect")
                .queryParam("token", tokenDto.getAccessToken())
                .build()
                .toUriString();

        log.info("AT와 RT(쿠키) 발급 완료. 프론트엔드로 리디렉션: {}", targetUrl);

        // 6. 리디렉션
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
