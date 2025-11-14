package com.multi.multi_semi.main_list.rating.service;

import com.multi.multi_semi.main_list.rating.dto.TopRatedPlaceDto;
import com.multi.multi_semi.review.dao.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingListServiceImpl implements RatingListService {

    private final ReviewMapper reviewMapper;

    @Override
    public List<TopRatedPlaceDto> getTopRatedPlaces(int limit) {
        List<TopRatedPlaceDto> list = reviewMapper.findTopRatedPlaceAgg(limit);  // 또는 네가 만든 집계 쿼리

        for (TopRatedPlaceDto dto : list) {
            fillAvgRate(dto);   // ★ 여기서 평균/반올림까지 완성
        }
        return list;
    }

    private void fillAvgRate(TopRatedPlaceDto dto) {
        Long sum = dto.getRateSum();
        Long cnt = dto.getRateCount();
        double avg = 0.0;
        if (cnt != null && cnt > 0 && sum != null) {
            avg = sum.doubleValue() / cnt;
            avg = Math.round(avg * 10.0) / 10.0;
        }
        dto.setAvgRate(avg);
    }
}
