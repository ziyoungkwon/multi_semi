package com.multi.multi_semi.review.dao;


import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReviewMapper {

    int countReview(@Param("placeNo") int placeNo);

    List<FavoriteResDto> selectReviewListWithPaging(@Param("selectCriteria")SelectCriteria selectCriteria, @Param("placeNo") int placeNo);


}
