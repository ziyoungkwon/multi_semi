package com.multi.multi_semi.review.controller;

import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.Pagenation;
import com.multi.multi_semi.common.paging.ResponseDtoWithPaging;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dto.ReviewDto;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<ResponseDto> findReviewListByPaging(@RequestParam(name = "offset", defaultValue = "1") String offset) {

        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), reviewService.selectReviewTotal());

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(reviewService.findReviewList(selectCriteria), selectCriteria);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 리스트 조회 성공", responseDtoWithPaging));
    }

    private SelectCriteria getSelectCriteria(int offset, int totalCount) {
        int limit = 10;
        int buttonAmount = 10;
        return Pagenation.getSelectCriteria(offset, totalCount, limit, buttonAmount);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseDto> findReviewByNo(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 상세조회 성공", reviewService.findReviewByNo(reviewId)));
    }

    @PostMapping("/reviews")
    @Transactional
    public ResponseEntity<ResponseDto> insertReview(@RequestBody ReviewReqDto reviewReqDto) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 등록 성공", reviewService.insertReview(reviewReqDto)));

    }

    @PutMapping("/reviews")
    @Transactional
    public ResponseEntity<ResponseDto> updateReview(@RequestBody ReviewReqDto reviewReqDto) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 수정 성공", reviewService.updateReview(reviewReqDto)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Transactional
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok(new ResponseDto((HttpStatus.NO_CONTENT), "리뷰 삭제 성공", reviewService.deleteReview(reviewId)));
    }

    @GetMapping("/reviews/mypage")
    public ResponseEntity<ResponseDto> findReviewByMemberIdPaging(@RequestParam(name = "offset", defaultValue = "1") String offset,
                                                                  @AuthenticationPrincipal CustomUser customUser) {

        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), reviewService.selectReviewTotal());

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(reviewService.findReviewByMemberId(customUser.getNo(), selectCriteria), selectCriteria);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "내가 쓴 리뷰 리스트 조회 성공", responseDtoWithPaging));
    }

    @GetMapping("/reviews/place/{placeId}")
    public ResponseEntity<ResponseDto> findReviewByPlaceId(@RequestParam(name = "offset", defaultValue = "1") String offset, @PathVariable("placeId") String placeId) {

        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), reviewService.selectReviewTotal());

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(reviewService.findReviewByPlaceId(placeId, selectCriteria), selectCriteria);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "장소별 리뷰 리스트 조회 성공", responseDtoWithPaging));
    }

    @GetMapping("/reviews/rating/{placeId}")
    public ResponseEntity<ResponseDto> getPlaceRate(@PathVariable("placeId") String placeId) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "장소별 리뷰 평점 계산 성공", reviewService.getPlaceRate(placeId)));
    }


    //관리자
    @GetMapping("/reviews-management")
    public ResponseEntity<ResponseDto> findReviewListAdminByPaging(@RequestParam(name = "offset", defaultValue = "1") String offset) {

        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), reviewService.selectReviewTotal());

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(reviewService.findReviewList(selectCriteria), selectCriteria);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 리스트 조회 성공", responseDtoWithPaging));
    }

    @GetMapping("/reviews-management/{reviewId}")
    public ResponseEntity<ResponseDto> findReviewAdminByNo(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 상세조회 성공", reviewService.findReviewByNo(reviewId)));
    }

    @PutMapping("/reviews-management")
    @Transactional
    public ResponseEntity<ResponseDto> updateReviewAdmin(@RequestBody ReviewReqDto reviewReqDto) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 수정 성공", reviewService.updateReview(reviewReqDto)));
    }

    @DeleteMapping("/reviews-management/{reviewId}")
    @Transactional
    public ResponseEntity<ResponseDto> deleteReviewAdmin(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok(new ResponseDto((HttpStatus.NO_CONTENT), "리뷰 삭제 성공", reviewService.deleteReview(reviewId)));
    }
}
