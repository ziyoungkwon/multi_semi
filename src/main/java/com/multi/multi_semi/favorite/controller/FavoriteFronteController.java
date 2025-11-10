package com.multi.multi_semi.favorite.controller;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FavoriteFronteController {

    @GetMapping("/favorites/list")
    public void favoriteList() {
        //예외 테스트할때
        // throw new IllegalStateException("뷰 컨트롤러에서 발생한 예외입니다.");
    }

    @GetMapping("/favorites/favorite")
    public void addFavorite() {

    }

    @GetMapping("/favorites/delete/{favoriteSeq}")
    public String deleteFavorite(@PathVariable("favoriteSeq") int favoriteSeq, Model model) {
        model.addAttribute("favoriteSeq", favoriteSeq);
        return "favorites/delete";
    }


}
