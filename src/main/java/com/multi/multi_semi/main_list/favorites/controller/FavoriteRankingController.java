package com.multi.multi_semi.main_list.favorites.controller;

import com.multi.multi_semi.main_list.favorites.dto.TopFavoritePlaceDto;
import com.multi.multi_semi.main_list.favorites.service.FavoriteRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class FavoriteRankingController {

    private final FavoriteRankingService favoriteRankingService;

    @GetMapping("/favorites")
    public ResponseEntity<List<TopFavoritePlaceDto>> getTop5Favorites() {
        List<TopFavoritePlaceDto> top5 = favoriteRankingService.getTopFavoritePlaces(5);
        return ResponseEntity.ok(top5);
    }
}