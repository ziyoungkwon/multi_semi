package com.multi.multi_semi.member.controller;


import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
//@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/no/{no}")
    public ResponseEntity<ResponseDto> findMemberByNo(@PathVariable("no") String no){
        Optional<MemberDto> member = memberService.findMemberByNo(Long.parseLong(no));

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }


    @GetMapping("/members/email/{email}")
    public ResponseEntity<ResponseDto> findMemberByEmail(@PathVariable("email") String email){
        Optional<MemberDto> member = memberService.findMemberByEmail(email);

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }




}
