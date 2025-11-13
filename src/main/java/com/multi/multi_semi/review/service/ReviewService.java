package com.multi.multi_semi.review.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dao.ReviewMapper;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Value;
>>>>>>> dev
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
<<<<<<< HEAD
=======
@Transactional
>>>>>>> dev
public class ReviewService {

    private final ReviewMapper reviewMapper;

<<<<<<< HEAD
    public List<ReviewResDto> findReviewList(SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewList(selectCriteria);
=======
    @Value("${image.url}")
    private String IMAGE_URL;

    public List<ReviewResDto> findReviewList() {

        List<ReviewResDto> reviewList = reviewMapper.findReviewList();
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }
>>>>>>> dev

        return reviewList;
    }

    public int selectReviewTotal() {

        int result = reviewMapper.selectReviewTotal();

        return result;
    }

    public ReviewResDto findReviewByNo(String reviewNo) {

        ReviewResDto review = reviewMapper.findReviewByNo(Integer.parseInt(reviewNo));
<<<<<<< HEAD
=======
        if (review.getImgUrl() != null && !review.getImgUrl().isEmpty()) {
            review.setImgUrl(IMAGE_URL + review.getImgUrl());
        }
>>>>>>> dev

        return review;
    }

<<<<<<< HEAD
=======

>>>>>>> dev
    public int insertReview(ReviewReqDto reviewReqDto) {

        int result = reviewMapper.insertReview(reviewReqDto);

<<<<<<< HEAD
=======

>>>>>>> dev
        return result;
    }

    public int updateReview(ReviewReqDto reviewReqDto) {

        int result = reviewMapper.updateReview(reviewReqDto);

        return result;
    }

    public int deleteReview(String reviewId) {

        int result = reviewMapper.deleteReview(Integer.parseInt(reviewId));

        return result;
    }

<<<<<<< HEAD
    public List<ReviewResDto> findReviewByMemberEmail(String memberEmail, SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByMemberEmail(memberEmail, selectCriteria);
=======
    public List<ReviewResDto> findReviewByMemberId(String memberId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByMemberId(memberId);
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }
>>>>>>> dev

        return reviewList;
    }

    public Double getPlaceRate(String placeId) {
        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceId(Integer.parseInt(placeId));

        if (reviewList == null || reviewList.isEmpty()) {
            return 0.0; // 리뷰 없을 때 0점
        }

        int sum = 0;
        for (ReviewResDto reviewResDto : reviewList) {
            sum += reviewResDto.getRate();
        }

        double avg = (double) sum / reviewList.size();
        return Math.round(avg * 10.0) / 10.0; // 소수점 1자리 반올림
    }

<<<<<<< HEAD
    public List<ReviewResDto> findReviewByPlaceId(String placeId, SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceIdPaging(Integer.parseInt(placeId), selectCriteria);
=======
    public List<ReviewResDto> findReviewByPlaceId(String placeId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceIdPaging(Integer.parseInt(placeId));
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }
>>>>>>> dev

        return reviewList;
    }
}
