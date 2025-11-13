package com.multi.multi_semi.auth.controller;


import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.auth.service.AuthService;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.jwt.TokenProvider;
import com.multi.multi_semi.common.jwt.dto.AccessTokenResponseDto;
import com.multi.multi_semi.common.jwt.dto.TokenDto;
import com.multi.multi_semi.common.jwt.service.TokenService;
import com.multi.multi_semi.member.dto.req.MemberReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
//@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDto> signup(@ModelAttribute MemberReqDto memberReqDto) {
        authService.signup(memberReqDto);
        return ok(new ResponseDto(HttpStatus.CREATED, "회원가입 성공", null));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDto> login(@ModelAttribute MemberReqDto memberReqDto) {

        System.out.println(">>>>>>>>>>>>>" + memberReqDto);
        TokenDto token = authService.login(memberReqDto); // AT, RT가 모두 담겨있음

        // 1. Refresh Token을 HttpOnly 쿠키로 생성
        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .maxAge(tokenProvider.getRefreshTokenExpirySeconds()) // [수정] 만료 시간 설정
                .path("/")      // 쿠키를 사용할 경로
                .httpOnly(true) // [핵심] JS 접근 불가
                .secure(true)   // (선택) HTTPS에서만 전송 (운영 시 true 권장)
                .sameSite("None") // (선택) CORS 환경용. (None + secure=true)
                .build();

        // 2. Access Token은 DTO에 담아 Body로 전송
        AccessTokenResponseDto atResponse = new AccessTokenResponseDto(token.getAccessToken());

        // 3. 응답: Set-Cookie 헤더에 RT 쿠키를, Body에 AT를 담아 보냄
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, rtCookie.toString()) // 쿠키 설정
                .body(new ResponseDto(HttpStatus.CREATED, "로그인 성공", atResponse)); // 본문 설정
    }

    // 브라우저의 HttpOnly RT 쿠키를 만료(삭제)시킵니다.
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToKen) {

        // 1. DB에서 RT 삭제
        tokenService.deleteRefreshToken(accessToKen);

        // 2. HttpOnly RT 쿠키 삭제 (Max-Age=0)
        ResponseCookie deleteRtCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0) // 즉시 만료
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteRtCookie.toString()) // 쿠키 삭제
                .body("로그아웃 성공 및 Refresh Token 삭제 완료");
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ResponseDto> refresh(
            @RequestHeader("Authorization") String expiredAccessToken, // 만료된 엑세스 토큰은 헤더로
            @CookieValue("refreshToken") String clientRefreshToken         // 리프레시 토큰은 쿠키로
            , @AuthenticationPrincipal CustomUser customUser
            ) {
        // 리프레시 토큰 검증 후 엑세스 토큰 재발급
        String newAccessToken = tokenService.refreshAccessToken(expiredAccessToken, clientRefreshToken);

        // 새 엑세스 토큰만 담아서 반환
        AccessTokenResponseDto responseDto = new AccessTokenResponseDto(newAccessToken);

        return ok().body(new ResponseDto(HttpStatus.OK, "Access Token 갱신 성공", responseDto));

    }

}