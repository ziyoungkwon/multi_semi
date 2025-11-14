package com.multi.multi_semi.review.dao;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.main_list.rating.dto.*;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewResDto> findReviewList();

    int selectReviewTotal();

    ReviewResDto findReviewByNo(@Param("no")int reviewNo);

    int insertReview(ReviewReqDto reviewReqDto);

    int updateReview(ReviewReqDto reviewReqDto);

    int deleteReview(@Param("no") int reviewNo);

    List<ReviewResDto> findReviewByMemberId(@Param("memberNo") String memberId);

    List<ReviewResDto> findReviewByPlaceId(@Param("placeNo") int placeId);

    List<ReviewResDto> findReviewByPlaceIdPaging(@Param("placeNo")int placeId);

    List<TopRatedPlaceDto> findTopRatedPlaceAgg(@Param("limit") int limit);
}
