package com.multi.multi_semi.review.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dto.ReviewDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FrontReviewController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${server.api-url:http://localhost:8090}")
    private String apiUrl;

    @GetMapping("/reviews/list")
    public String reviewListPage(@RequestParam(name = "offset", defaultValue = "1") String offset, Model model) {
        try {
            // ✅ 1. 내부 API 호출
            String url = apiUrl + "/api/v1/reviews?offset=" + offset;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // ✅ 2. JSON 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");

            JsonNode reviewList = dataNode.path("data");
            JsonNode pagingInfo = dataNode.path("pageInfo");

            // ✅ 3. TypeReference로 안전하게 변환
            List<ReviewResDto> reviews = objectMapper.convertValue(
                    reviewList, new TypeReference<List<ReviewResDto>>() {}
            );
            SelectCriteria page = objectMapper.convertValue(
                    pagingInfo, new TypeReference<SelectCriteria>() {}
            );

            // ✅ 4. Model에 전달
            model.addAttribute("reviews", reviews);
            model.addAttribute("page", page);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 목록을 불러오는 중 오류가 발생했습니다.");
        }

        return "reviews/reviews";
    }
}
