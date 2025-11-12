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


    @GetMapping("/members")
    public ResponseEntity<ResponseDto> findMemberByEmail(@AuthenticationPrincipal CustomUser customUser){
        String email = customUser.getEmail();

        Optional<MemberDto> member = memberService.findMemberByEmail(email);

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
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
    public ResponseEntity<ResponseDto> deleteMember(@AuthenticationPrincipal CustomUser customUser){
        String email = customUser.getEmail();

        memberService.deleteMemberByEmail(email);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원 탈퇴 성공", null));
    }



    @PatchMapping("/admin/members/{email}/edit-info")
    public ResponseEntity<ResponseDto> adminUpdateMemberInfo(@PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){

        memberService.updateMemberInfo(email, updateMemberReqDto);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원정보 수정 성공(관리자)", null));
    }


}
