package com.multi.multi_semi.member.controller;


import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.jwt.service.TokenService;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.req.UpdateMemberReqDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @GetMapping("/members/{no}")
    public ResponseEntity<ResponseDto> findMemberByNo(@PathVariable("no") String no){
        Optional<MemberDto> member = memberService.findMemberByNo(Long.parseLong(no));

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }


    @GetMapping("/members")
    public ResponseEntity<ResponseDto> findMemberByEmail(@AuthenticationPrincipal CustomUser customUser){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> customUser = " + customUser);
        String email = customUser.getEmail();

        Optional<MemberDto> member = memberService.findMemberByEmail(email);

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member.get()));
    }




    @PatchMapping("/members/edit-info")
    public ResponseEntity<ResponseDto> updateMemberInfo(@AuthenticationPrincipal CustomUser customUser, @RequestBody UpdateMemberReqDto updateMemberReqDto){
        String email = customUser.getEmail();

        memberService.updateMemberInfo(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원정보 수정 성공", null));
    }


    @PatchMapping("/members/edit-pwd")
    public ResponseEntity<ResponseDto> updateMemberPwd(@AuthenticationPrincipal CustomUser customUser, @RequestBody UpdateMemberReqDto updateMemberReqDto){
        String email = customUser.getEmail();

        memberService.updateMemberPwd(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원비밀번호 수정 성공", null));
    }

    @DeleteMapping("/members")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal CustomUser customUser, @RequestHeader("Authorization") String accessToKen){
        String email = customUser.getEmail();


        // 1. DB에서 RT 삭제
        tokenService.deleteRefreshToken(accessToKen);

        memberService.deleteMemberByEmail(email);

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
                .body("회원탈퇴, 로그아웃 성공 및 Refresh Token 삭제 완료");

    }



    @PatchMapping("/admin/members/{email}/edit-info")
    public ResponseEntity<ResponseDto> adminUpdateMemberInfo(@PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){

        memberService.updateMemberInfo(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원정보 수정 성공(관리자)", null));
    }


    @GetMapping("/members/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 필요"));
        }
        return ResponseEntity.ok(Map.of("email", customUser.getEmail(), "id", memberService.findMemberByEmail(customUser.getEmail()).get().getId()));
    }



}
