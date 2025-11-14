package com.multi.multi_semi.main_list.favorites.service;

import com.multi.multi_semi.favorite.dao.FavoriteMapper;
import com.multi.multi_semi.main_list.favorites.dto.TopFavoritePlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteRankingServiceImpl implements FavoriteRankingService {

    private final FavoriteMapper favoriteMapper;

    @Override
    public List<TopFavoritePlaceDto> getTopFavoritePlaces(int limit) {
        return favoriteMapper.findTopFavoritePlaces(limit);
    }
}