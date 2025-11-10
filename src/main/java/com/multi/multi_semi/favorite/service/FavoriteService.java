package com.multi.multi_semi.favorite.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dao.FavoriteMapper;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public List<FavoriteAllDto> getAllFavorite() {
        return favoriteMapper.selectList();
    }


    public Object selectFavoriteListWithPaging(SelectCriteria selectCriteria) {

        List<FavoriteResDto> list = favoriteMapper.selectFavoriteListWithPaging(selectCriteria);
        return list;
    }

    public int getFavoriteCount() { return favoriteMapper.countFavorite();}

    public Object insertFavorite(FavoriteReqDto favoriteDto) {

        int result = 0;
        result = favoriteMapper.insertFavorite(favoriteDto);
        log.info("[favoriteService] result > 0 성공: " + result);
        return (result > 0) ? "favorite 등록 성공" : "favorite 등록 실패";
    }

    public Object deleteFavorite(int favoriteId) {
        int result = 0;
        result = favoriteMapper.deleteFavorite(favoriteId);
        log.info("[favoriteService] result > 0 성공: " + result);
        return (result > 0) ? "favorite 삭제 성공" : "favorite 삭제 실패";
    }
}
