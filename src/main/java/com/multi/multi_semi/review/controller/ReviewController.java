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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
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
    @Transactional
    public ResponseEntity<ResponseDto> insertReview(@ModelAttribute ReviewReqDto reviewReqDto) {
        try {
            MultipartFile file = reviewReqDto.getImgFile();

            String fileName = null;
            if (file != null && !file.isEmpty()) {
                // UUID + 원본 파일명으로 중복 방지
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                // 저장 경로 생성
                File dest = new File("/Users/kwonjiyoung/Documents/multi_semi/", fileName);
                file.transferTo(dest); // 실제 파일 저장
            }

            // 파일명이 있으면 DTO에 세팅
            reviewReqDto.setImgUrl(fileName);

            // 서비스 호출
            reviewService.insertReview(reviewReqDto);

            return ResponseEntity.ok(new ResponseDto(HttpStatus.CREATED, "리뷰 등록 성공", null));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패", null));
        }
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
    public ResponseEntity<ResponseDto> findReviewByMemberIdPaging(@AuthenticationPrincipal CustomUser customUser) {


        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "내가 쓴 리뷰 리스트 조회 성공", reviewService.findReviewByMemberId(customUser.getEmail())));
    }

    @GetMapping("/reviews/place/{placeId}")
    public ResponseEntity<ResponseDto> findReviewByPlaceId(@PathVariable("placeId") String placeId) {

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "장소별 리뷰 리스트 조회 성공", reviewService.findReviewByPlaceId(placeId)));
    }

    @GetMapping("/reviews/rating/{placeId}")
    public ResponseEntity<ResponseDto> getPlaceRate(@PathVariable("placeId") String placeId) {

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "장소별 리뷰 평점 계산 성공", reviewService.getPlaceRate(placeId)));
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
