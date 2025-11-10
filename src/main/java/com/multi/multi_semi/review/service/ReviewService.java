package com.multi.multi_semi.review.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.review.dao.ReviewMapper;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;

    public List<ReviewResDto> findReviewList(SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewList(selectCriteria);

        return reviewList;
    }

    public int selectReviewTotal() {

        int result = reviewMapper.selectReviewTotal();

        return result;
    }

    public ReviewResDto findReviewByNo(String reviewNo) {

        ReviewResDto review = reviewMapper.findReviewByNo(Integer.parseInt(reviewNo));

        return review;
    }

    public int insertReview(ReviewReqDto reviewReqDto) {

        int result = reviewMapper.insertReview(reviewReqDto);

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

    public List<ReviewResDto> findReviewByMemberId(String memberId, SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByMemberId(memberId, selectCriteria);

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

    public List<ReviewResDto> findReviewByPlaceId(String placeId, SelectCriteria selectCriteria) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceIdPaging(Integer.parseInt(placeId), selectCriteria);

        return reviewList;
    }
}
