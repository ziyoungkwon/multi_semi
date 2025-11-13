package com.multi.multi_semi.member.controller;


import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.req.UpdateMemberReqDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/members/{no}")
    public ResponseEntity<ResponseDto> findMemberByNo(@PathVariable("no") String no){
        Optional<MemberDto> member = memberService.findMemberByNo(Long.parseLong(no));

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }


    @GetMapping("/members/{email}")
    public ResponseEntity<ResponseDto> findMemberByEmail(@PathVariable("email") String email){
        Optional<MemberDto> member = memberService.findMemberByEmail(email);

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }




    @PatchMapping("/members/{email}/edit-info")
    public ResponseEntity<ResponseDto> updateMemberInfo(@PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){
        memberService.updateMemberInfo(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원정보 수정 성공", null));
    }


    @PatchMapping("/members/{email}/edit-pwd")
    public ResponseEntity<ResponseDto> updateMemberPwd(@PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){
        memberService.updateMemberPwd(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원비밀번호 수정 성공", null));
    }

    @DeleteMapping("/members/{email}")
    public ResponseEntity<ResponseDto> deleteMember(@PathVariable("email") String email){
        memberService.deleteMemberByEmail(email);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원 탈퇴 성공", null));
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
