package com.multi.multi_semi.review.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dao.FavoriteMapper;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.review.dao.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewMapper reviewMapper;


    public Object selectReviewListWithPaging(SelectCriteria selectCriteria, int placeNo) {

        List<FavoriteResDto> list = reviewMapper.selectReviewListWithPaging(selectCriteria,placeNo);
        return list;
    }

    public int getReviewCount(int placeNo) { return reviewMapper.countReview(placeNo);}


}
