package com.multi.multi_semi.review.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import com.multi.multi_semi.place.service.PlaceService;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class FrontReviewController {

    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper;
    private final PlaceService placeService;

    @Value("${server.api-url:http://localhost:8090}")
    private String apiUrl;

    private final String BASE_URL = "http://localhost:8090/api/v1/reviews/";

    /** ✅ 리뷰 목록 페이지 */
    @GetMapping("/list")
    public String reviewListPage(Model model) {
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();

            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl + "/api/v1/reviews", String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");

            List<ReviewResDto> reviews = objectMapper.convertValue(dataNode, new TypeReference<List<ReviewResDto>>() {});
            model.addAttribute("reviews", reviews);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 목록을 불러오는 중 오류가 발생했습니다.");
        }

        return "reviews/reviews";
    }

    /** ✅ 리뷰 상세 페이지 */
    @GetMapping("/{reviewId}")
    public String reviewDetail(@PathVariable("reviewId") String reviewId, Model model) {
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<ResponseDto> response = restTemplate.getForEntity(BASE_URL + reviewId, ResponseDto.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ReviewResDto review = objectMapper.convertValue(response.getBody().getData(), ReviewResDto.class);
                model.addAttribute("review", review);
            } else {
                model.addAttribute("error", "리뷰 정보를 불러오지 못했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 상세 정보를 불러오는 중 오류가 발생했습니다.");
        }

        return "reviews/review-detail";
    }


    // 📍 리뷰 등록 폼 페이지
    @GetMapping("/form")
    public String reviewFormPage(Model model) {
        List<PlaceDto> placeList = placeService.findAllPlaces();
        model.addAttribute("places", placeList);
        return "reviews/review-form";
    }

    /** 리뷰 수정 페이지 이동 (단순 렌더링) */
    @GetMapping("/edit/{reviewId}")
    public String reviewEditPage(@PathVariable("reviewId") Long reviewId) {
        return "reviews/review-update";
    }

    @GetMapping("/mypage")
    public String myReviewPage() {
        return "reviews/review-mypage";   // templates/review/review-mypage.html
    }


}
