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
import org.springframework.http.MediaType;
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
    public ResponseEntity<ResponseDto> insertReview(@ModelAttribute ReviewReqDto reviewReqDto, @AuthenticationPrincipal CustomUser customUser) {
        try {
            MultipartFile file = reviewReqDto.getImgFile();
            String fileName = null;

            // ✅ 이미지 저장 처리
            if (file != null && !file.isEmpty()) {
                // 저장 경로 (본인 프로젝트 경로 맞게 수정 가능)
                String uploadDir = "/Users/kwonjiyoung/Documents/multi_semi_project/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                // UUID + 파일명
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir, fileName);
                file.transferTo(dest);

                reviewReqDto.setImgUrl("/uploads/" + fileName); // 웹 접근 경로 (원하면 DB에 전체 경로로 저장 가능)
            }

            reviewReqDto.setWriterEmail(customUser.getEmail());
            reviewReqDto.setModifiedBy(customUser.getEmail());

            // ✅ DB 저장 호출
            reviewService.insertReview(reviewReqDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK, "리뷰 등록 성공", null));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패", null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 등록 중 오류 발생", null));
        }
    }


    @PutMapping(value = "/reviews/{reviewId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto> updateReview(
            @PathVariable Long reviewId,
            @ModelAttribute ReviewReqDto reviewReqDto,
            @AuthenticationPrincipal CustomUser customUser) {

        try {

            MultipartFile file = reviewReqDto.getImgFile();
            String fileName = null;

            // ✅ 이미지 저장 처리
            if (file != null && !file.isEmpty()) {
                // 저장 경로 (본인 프로젝트 경로 맞게 수정 가능)
                String uploadDir = "/Users/kwonjiyoung/Documents/multi_semi_project/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                // UUID + 파일명
                fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir, fileName);
                file.transferTo(dest);

                reviewReqDto.setImgUrl("/uploads/" + fileName); // 웹 접근 경로 (원하면 DB에 전체 경로로 저장 가능)
            }

            reviewReqDto.setNo(reviewId); // ✅ 수정할 리뷰 번호 주입
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

//    @GetMapping("/reviews/rating/{placeId}")
//    public ResponseEntity<ResponseDto> getPlaceRate(@PathVariable("placeId") String placeId) {
//
//        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, "장소별 리뷰 평점 계산 성공", reviewService.getPlaceRate(placeId)));
//    }


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
}
