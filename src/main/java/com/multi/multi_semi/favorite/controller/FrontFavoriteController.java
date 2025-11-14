package com.multi.multi_semi.favorite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FrontFavoriteController {

//    @GetMapping("/favorites/list")
//    public void favoriteList() {
//        //예외 테스트할때
//        // throw new IllegalStateException("뷰 컨트롤러에서 발생한 예외입니다.");
//    }

    @GetMapping("/favorites/favorite")
    public void addFavorite() {

    }

    @GetMapping("/favorites/delete/{favoriteSeq}")
    public String deleteFavorite(@PathVariable("favoriteSeq") int favoriteSeq, Model model) {
        model.addAttribute("favoriteSeq", favoriteSeq);
        return "favorites/delete";
    }


    /**
     * [신규 추가] '내 즐겨찾기' 페이지
     * URL: /favorites/list
     */
    @GetMapping("/favorites/list")
    public String myFavoritesPage(Model model) {
        model.addAttribute("contentFragment", "mypage/my-favorites"); // my-favorites.html 조각
        model.addAttribute("activePage", "my-favorites"); // 사이드바 '내 즐겨찾기' 활성화
        return "layout/mypage-layout"; // 공통 레이아웃 반환
    }


}
