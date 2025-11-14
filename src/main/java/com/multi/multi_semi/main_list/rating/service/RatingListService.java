package com.multi.multi_semi.main_list.rating.service;

import com.multi.multi_semi.main_list.rating.dto.TopRatedPlaceDto;

import java.util.List;

public interface RatingListService {
    List<TopRatedPlaceDto> getTopRatedPlaces(int limit);
}
