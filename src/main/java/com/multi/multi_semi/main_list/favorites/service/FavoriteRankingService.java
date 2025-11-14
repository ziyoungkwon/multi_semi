package com.multi.multi_semi.main_list.favorites.service;

import com.multi.multi_semi.main_list.favorites.dto.TopFavoritePlaceDto;

import java.util.List;

public interface FavoriteRankingService {

    List<TopFavoritePlaceDto> getTopFavoritePlaces(int limit);
}
