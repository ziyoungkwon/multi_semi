package com.multi.multi_semi.favorite.dao;


import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.main_list.favorites.dto.*;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface FavoriteMapper {

    ArrayList<FavoriteAllDto> selectList();

    int countFavorite();

    List<FavoriteResDto> selectFavoriteListWithPaging(SelectCriteria selectCriteria);

    int insertFavorite(FavoriteReqDto favoriteDto);

    int deleteFavorite(int favoriteId);

    List<TopFavoritePlaceDto> findTopFavoritePlaces(@Param("limit") int limit);
}
