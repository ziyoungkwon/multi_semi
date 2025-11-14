package com.multi.multi_semi.review.dao;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.main_list.rating.dto.*;
import com.multi.multi_semi.review.dto.ReviewReqDto;
import com.multi.multi_semi.review.dto.ReviewResDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReviewMapper {
    List<ReviewResDto> findReviewList();

    int selectReviewTotal();

    ReviewResDto findReviewByNo(@Param("no")Long reviewNo);

    int insertReview(ReviewReqDto reviewReqDto);

    int updateReview(ReviewReqDto reviewReqDto);
    int countReview(@Param("placeNo") int placeNo);

    int deleteReview(@Param("no") int reviewNo);

    List<ReviewResDto> selectReviewListWithPaging(@Param("selectCriteria")SelectCriteria selectCriteria, @Param("placeNo") int placeNo);

    List<ReviewResDto> findReviewByMemberId(@Param("memberNo") String memberId);

    List<ReviewResDto> findReviewByPlaceId(@Param("placeNo") Long placeId);

    List<ReviewResDto> findReviewByPlaceIdPaging(@Param("placeNo")int placeId);

    List<TopRatedPlaceDto> findTopRatedPlaceAgg(@Param("limit") int limit);
}
