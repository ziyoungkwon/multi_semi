package com.multi.multi_semi.review.controller;

import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.Pagenation;
import com.multi.multi_semi.common.paging.ResponseDtoWithPaging;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<ResponseDto> findReviewList() {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 리스트 조회 성공", reviewService.findReviewList()));
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseDto> findReviewByNo(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 상세조회 성공", reviewService.findReviewByNo(reviewId)));
    }

//    @PostMapping("/reviews")
//    @Transactional
//    public ResponseEntity<ResponseDto> insertReview(@RequestBody ReviewReqDto reviewReqDto) {
//
//        return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 등록 성공", reviewService.insertReview(reviewReqDto)));
//
//    }

    @PostMapping(value = "/reviews", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseDto> insertReview(@ModelAttribute ReviewReqDto reviewReqDto, @AuthenticationPrincipal CustomUser customUser) {
        try {
            reviewReqDto.setWriterEmail(customUser.getEmail());
            reviewReqDto.setModifiedBy(customUser.getEmail());

            reviewService.insertReview(reviewReqDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK, "리뷰 등록 성공", null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 등록 중 오류 발생", null));
        }
    }


    @PutMapping(value = "/reviews/{reviewId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @ModelAttribute ReviewReqDto reviewReqDto,
            @AuthenticationPrincipal CustomUser customUser) {

        try {
            reviewReqDto.setNo(reviewId);
            reviewReqDto.setModifiedBy(customUser.getEmail());

            reviewService.updateReview(reviewReqDto);

            return ResponseEntity.ok(
                    new ResponseDto(HttpStatus.OK, "리뷰 수정 성공", null)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 수정 실패", null));
        }
    }


    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok(new ResponseDto((HttpStatus.NO_CONTENT), "리뷰 삭제 성공", reviewService.deleteReview(reviewId)));
    }

    @GetMapping("/reviews/my")
    public ResponseEntity<ResponseDto> findReviewByMemberId(@AuthenticationPrincipal CustomUser customUser) {


        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "내가 쓴 리뷰 리스트 조회 성공", reviewService.findReviewByMemberId(customUser.getEmail())));
    }

    @GetMapping("/reviews/place/{placeId}")
    public ResponseEntity<ResponseDto> findReviewByPlaceId(@PathVariable("placeId") String placeId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "장소별 리뷰 리스트 조회 성공", reviewService.findReviewByPlaceId(placeId)));
    }

    @GetMapping("/reviews/rating/{placeId}")
    public ResponseEntity<ResponseDto> getPlaceRate(@PathVariable("placeId") String placeId) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "장소별 리뷰 평점 계산 성공", reviewService.getPlaceRate(Long.parseLong(placeId))));
    }


    //관리자
    @GetMapping("/reviews-management")
    public ResponseEntity<ResponseDto> findReviewListAdminByPaging() {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 리스트 조회 성공", reviewService.findReviewList()));
    }

    @GetMapping("/reviews-management/{reviewId}")
    public ResponseEntity<ResponseDto> findReviewAdminByNo(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "리뷰 상세조회 성공", reviewService.findReviewByNo(reviewId)));
    }

    @PutMapping("/reviews-management")
    public ResponseEntity<ResponseDto> updateReviewAdmin(@RequestBody ReviewReqDto reviewReqDto) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 수정 성공", reviewService.updateReview(reviewReqDto)));
    }

    @DeleteMapping("/reviews-management/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReviewAdmin(@PathVariable("reviewId") String reviewId) {

        return ResponseEntity.ok(new ResponseDto((HttpStatus.NO_CONTENT), "리뷰 삭제 성공", reviewService.deleteReview(reviewId)));
    }

    @GetMapping("/reviews/place-detail/{placeNo}")
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
