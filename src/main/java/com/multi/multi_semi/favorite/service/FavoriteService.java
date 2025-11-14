package com.multi.multi_semi.favorite.service;

import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dao.FavoriteMapper;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.dto.FavoriteResDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final MemberService memberService;

    public List<FavoriteAllDto> getAllFavorite() {
        return favoriteMapper.selectList();
    }


    public Object selectFavoriteListWithPaging(String email, SelectCriteria selectCriteria) {

        List<FavoriteResDto> list = favoriteMapper.selectFavoriteListWithPaging(email,selectCriteria);
        return list;
    }

    public int getFavoriteCount(String email) { return favoriteMapper.countFavorite(email);}

    public Object insertFavorite(FavoriteReqDto favoriteDto) {


        String email = favoriteDto.getMemEmail();
        String placeNo = favoriteDto.getPlaceNo();

        int exists = favoriteMapper.existsFavorite(email, Long.parseLong(placeNo));

        if (exists > 0) {
            throw new RuntimeException("이미 즐겨찾기를 등록하셨습니다.");
        }

        int result = favoriteMapper.insertFavorite(favoriteDto);

        if (result > 0) {
            return "favorite 등록 성공";
        } else {
            return "favorite 등록 실패";
        }
    }

    public Object deleteFavorite(int favoriteId) {
        int result = 0;
        result = favoriteMapper.deleteFavorite(favoriteId);
        log.info("[favoriteService] result > 0 성공: " + result);
        return (result > 0) ? "favorite 삭제 성공" : "favorite 삭제 실패";
    }
}
