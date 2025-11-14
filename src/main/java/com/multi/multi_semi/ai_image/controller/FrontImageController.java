package com.multi.multi_semi.ai_image.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FrontImageController {

    /**
     * 메인 페이지 로딩 (기존과 동일)
     */
    @GetMapping("/generate-request")
    public String aiImagePage() {
        return "/ai-image/generate-ai-image";
    }


//    @GetMapping("/ai-images/my")
//    public String myAiImages() {
//        return "/ai-image/my-ai-image";
//    }


    // "내가 생성한 이미지" 페이지
    @GetMapping("/ai-images/my")
    public String myAiImagePage(Model model) {
        model.addAttribute("contentFragment", "mypage/my-ai-image");
        model.addAttribute("activePage", "my-ai-images"); // 'my-ai-images'로 변경
        return "layout/mypage-layout";
    }





}
