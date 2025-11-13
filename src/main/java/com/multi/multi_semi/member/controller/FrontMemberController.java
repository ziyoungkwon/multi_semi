package com.multi.multi_semi.member.controller;

import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FrontMemberController {

    private final MemberService memberService;

    @GetMapping("/members/{email}/edit-info")
    public String updateMemberInfo(Model model, @PathVariable("email") String email) {
        MemberDto memberDto = memberService.findMemberByEmail(email).get();
        model.addAttribute("member", memberDto);
        model.addAttribute("currentEmail", memberDto.getEmail());
        return "member/edit-info";
    }


    @GetMapping("/members/{email}/edit-pwd")
    public String updateMemberPwd(Model model, @PathVariable("email") String email) {
        MemberDto memberDto = memberService.findMemberByEmail(email).get();
        model.addAttribute("member", memberDto);
        return "member/edit-pwd";
    }




}
