package com.multi.multi_semi.place.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/places")
public class FrontPlaceController {

    @GetMapping("/{placeNo:\\d+}")
    public String placeDetail(@PathVariable("placeNo") int placeNo, Model model) {
        model.addAttribute("placeNo", placeNo);
        return "places/detail";
    }

    @GetMapping("/list")
    public String placeList() {
        return "places/place-list";
    }

    @GetMapping("/district")
    public String placeListByDistrict() {
        return "places/place-district";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "places/place-search";
    }

    @GetMapping("/rate")
    public String ratePage() {
        return "places/place-rate";
    }

    @GetMapping("/search-result")
    public String searchResultPage(@RequestParam("keyword") String keyword, Model model) {

        model.addAttribute("keyword", keyword);  // 화면으로 keyword 전달

        return "places/place-search-result"; // place-search-result.html
    }
}
