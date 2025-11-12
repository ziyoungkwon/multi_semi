package com.multi.multi_semi.review.controller;

import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.Pagenation;
import com.multi.multi_semi.common.paging.ResponseDtoWithPaging;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/reviews/{placeNo}")
    public ResponseEntity<ResponseDto> selectReviewListWithPaging(@RequestParam(name = "offset", defaultValue = "1") String offset,@PathVariable("placeNo") int placeNo) {

        log.info("[FavoriteController] selectFavoriteListWithPaging offset: {}", offset);

        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), reviewService.getReviewCount(placeNo));

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(reviewService.selectReviewListWithPaging(selectCriteria,placeNo), selectCriteria);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "조회성공", responseDtoWithPaging));
    }

    private SelectCriteria getSelectCriteria(int offset, int totalCount) {

        int limit = 10;
        int buttonAmount = 10;

        return Pagenation.getSelectCriteria(offset, totalCount, limit, buttonAmount);
    }
}
