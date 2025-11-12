package com.multi.multi_semi.review.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class FrontReviewController {

    // ✅ RestTemplateBuilder로 교체
    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper;

    @Value("${server.api-url:http://localhost:8090}")
    private String apiUrl;

    private final String BASE_URL = "http://localhost:8090/api/v1/reviews/";

    /**
     * 리뷰 목록 페이지
     */
    @GetMapping("/list")
    public String reviewListPage(@RequestParam(name = "offset", defaultValue = "1") String offset, Model model) {
        try {
            // ✅ 1. RestTemplateBuilder 사용 — 스프링 자동 설정된 모듈 포함
            RestTemplate restTemplate = restTemplateBuilder.build();

            // ✅ 2. 내부 API 호출
            String url = apiUrl + "/api/v1/reviews?offset=" + offset;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // ✅ 3. JSON 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");
            JsonNode reviewList = dataNode.path("data");
            JsonNode pagingInfo = dataNode.path("pageInfo");

            // ✅ 4. TypeReference로 안전하게 변환
            List<ReviewResDto> reviews = objectMapper.convertValue(
                    reviewList, new TypeReference<List<ReviewResDto>>() {}
            );
            SelectCriteria page = objectMapper.convertValue(
                    pagingInfo, new TypeReference<SelectCriteria>() {}
            );

            // ✅ 5. Model에 전달
            model.addAttribute("reviews", reviews);
            model.addAttribute("page", page);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 목록을 불러오는 중 오류가 발생했습니다.");
        }

        return "reviews/reviews";
    }

    /**
     * 리뷰 상세 페이지
     */
    @GetMapping("/{reviewId}")
    public String reviewDetail(@PathVariable("reviewId") String reviewId, Model model) {
        try {
            // ✅ RestTemplateBuilder로 생성
            RestTemplate restTemplate = restTemplateBuilder.build();

            // ✅ 내부 API 호출
            ResponseEntity<ResponseDto> response =
                    restTemplate.getForEntity(BASE_URL + reviewId, ResponseDto.class);

            // ✅ 응답 상태 확인
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // ResponseDto의 data를 ReviewResDto로 매핑
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

}
