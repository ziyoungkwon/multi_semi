package com.multi.multi_semi.main_list.rating.controller;

import com.multi.multi_semi.common.*;
import com.multi.multi_semi.main_list.rating.dto.TopRatedPlaceDto;
import com.multi.multi_semi.main_list.rating.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class RatingListController {

    private final RatingListService ratingService;

    @GetMapping("/rating")
    public ResponseEntity<ResponseDto> getTop5() {
        List<TopRatedPlaceDto> top5 = ratingService.getTopRatedPlaces(5);
        return ResponseEntity.ok(
                new ResponseDto(HttpStatus.OK, "평점 TOP5 조회 성공", top5)
        );
    }
}
