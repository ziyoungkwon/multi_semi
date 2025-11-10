package com.multi.multi_semi.place.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PlaceFrontController {

    @GetMapping("/places/{placeNo}")
    public String placeDetail(@PathVariable("placeNo") int placeNo, Model model) {
        model.addAttribute("placeNo", placeNo);
        return "places/detail";
    }
}
