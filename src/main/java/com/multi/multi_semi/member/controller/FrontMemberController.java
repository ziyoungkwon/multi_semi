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

//    @GetMapping("/members/edit-info")
//    public String updateMemberInfo() {
//        return "member/edit-info";
//    }


    @GetMapping("/members/edit-pwd")
    public String updateMemberPwd(Model model, @AuthenticationPrincipal CustomUser customUser) {
        String email = customUser.getEmail();
        MemberDto memberDto = memberService.findMemberByEmail(email).get();
        model.addAttribute("member", memberDto);
        return "members/edit-pwd";
    }


//    @GetMapping("/mypage")
//    public String updateMemberPage() {
//
//        return "member/mypage";
//    }



    @GetMapping("/admin/members/{email}/edit-info")
    public String adminUpdateMemberInfo(Model model , @PathVariable("email") String email, @RequestBody UpdateMemberReqDto updateMemberReqDto){

        MemberDto memberDto = memberService.findMemberByEmail(email).get();

        model.addAttribute("member", memberDto);
        model.addAttribute("currentEmail", memberDto.getEmail());

        return "/admin/member/edit-info";
    }







    // "회원 정보" 페이지
    @GetMapping("/mypage")
    public String myInfoPage(Model model) {
        model.addAttribute("contentFragment", "mypage/info"); // 1. 컨텐츠 조각 경로
        model.addAttribute("activePage", "info"); // 2. 활성화할 사이드바 메뉴
        return "layout/mypage-layout"; // 3. 공통 레이아웃 반환
    }

    // "회원정보 수정" 페이지
    @GetMapping("/members/edit-info")
    public String editInfoPage(Model model) {
        model.addAttribute("contentFragment", "mypage/edit-info");
        model.addAttribute("activePage", "edit-info"); // 'edit-info'로 변경
        return "layout/mypage-layout";
    }

    // "내가 생성한 이미지" 페이지
//    @GetMapping("/ai-images/my")
//    public String myAiImagePage(Model model) {
//        model.addAttribute("contentFragment", "mypage/my-ai-image");
//        model.addAttribute("activePage", "my-ai-images"); // 'my-ai-images'로 변경
//        return "layout/mypage-layout";
//    }

}
