package com.multi.multi_semi.member.controller;

import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.dto.req.UpdateMemberReqDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class FrontMemberController {

    private final MemberService memberService;

    @GetMapping("/members/edit-info")
    public String updateMemberInfo(Model model, @AuthenticationPrincipal CustomUser customUser) {
        String email = customUser.getEmail();
        MemberDto memberDto = memberService.findMemberByEmail(email).get();
        model.addAttribute("member", memberDto);
        model.addAttribute("currentEmail", memberDto.getEmail());
        return "member/edit-info";
    }


    @GetMapping("/members/edit-pwd")
    public String updateMemberPwd(Model model, @AuthenticationPrincipal CustomUser customUser) {
        String email = customUser.getEmail();
        MemberDto memberDto = memberService.findMemberByEmail(email).get();
        model.addAttribute("member", memberDto);
        return "member/edit-pwd";
    }


    @GetMapping("/admin/members/{email}/edit-info")
    public String adminUpdateMemberInfo(Model model , @PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){

        MemberDto memberDto = memberService.findMemberByEmail(email).get();

        model.addAttribute("member", memberDto);
        model.addAttribute("currentEmail", memberDto.getEmail());

        return "/admin/member/edit-info";
    }



}
