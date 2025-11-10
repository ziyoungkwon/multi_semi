package com.multi.multi_semi.favorite.dao;


import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface FavoriteMapper {

    ArrayList<FavoriteAllDto> selectList();

    int countFavorite();

    List<FavoriteResDto> selectFavoriteListWithPaging(SelectCriteria selectCriteria);

    int insertFavorite(FavoriteReqDto favoriteDto);

    int deleteFavorite(int favoriteId);
}
