package com.multi.multi_semi.member.controller;


import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ResponseDto> findMemberId(@PathVariable("memberId") String memberId){
        Optional<MemberDto> member = memberService.findByMemberId(memberId);

        if(member.isEmpty()){
            return ResponseEntity.ok(new ResponseDto(HttpStatus.NO_CONTENT, "회원 못 찾음", null));
        }

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "회원조회성공", member));
    }




}
