package com.multi.multi_semi.review.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.review.dao.ReviewMapper;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReviewMapper reviewMapper;


    @Value("${image.image-url}")
    private String IMAGE_URL;

    public List<ReviewResDto> findReviewList() {

        List<ReviewResDto> reviewList = reviewMapper.findReviewList();
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }
        return reviewList;
    }

    public int selectReviewTotal() {

        int result = reviewMapper.selectReviewTotal();

        return result;
    }

    public ReviewResDto findReviewByNo(String reviewNo) {

        ReviewResDto review = reviewMapper.findReviewByNo(Integer.parseInt(reviewNo));
        if (review.getImgUrl() != null && !review.getImgUrl().isEmpty()) {
            review.setImgUrl(IMAGE_URL + review.getImgUrl());
        }

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

    public List<ReviewResDto> findReviewByMemberId(String memberId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByMemberId(memberId);
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }

        return reviewList;
    }

    public Double getPlaceRate(Long placeId) {
        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceId(placeId);

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

    public List<ReviewResDto> findReviewByPlaceId(String placeId) {

        List<ReviewResDto> reviewList = reviewMapper.findReviewByPlaceIdPaging(Integer.parseInt(placeId));
        for(int i = 0 ; i < reviewList.size() ; i++) {
            reviewList.get(i).setImgUrl(IMAGE_URL + reviewList.get(i).getImgUrl());
        }

        return reviewList;
    }

    public Object selectReviewListWithPaging(SelectCriteria selectCriteria, int placeNo) {

        List<ReviewResDto> list = reviewMapper.selectReviewListWithPaging(selectCriteria,placeNo);
        return list;
    }

    public int getReviewCount(int placeNo) { return reviewMapper.countReview(placeNo);}

}
