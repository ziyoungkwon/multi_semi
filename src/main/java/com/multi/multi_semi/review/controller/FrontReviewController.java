package com.multi.multi_semi.review.controller;

import com.multi.multi_semi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FrontReviewController {

    private final ReviewService reviewService;

}
