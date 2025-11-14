package com.multi.multi_semi.favorite.controller;

import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.ResponseDto;
import com.multi.multi_semi.common.paging.Pagenation;
import com.multi.multi_semi.common.paging.ResponseDtoWithPaging;
import com.multi.multi_semi.common.paging.SelectCriteria;
import com.multi.multi_semi.favorite.dto.FavoriteAllDto;
import com.multi.multi_semi.favorite.dto.FavoriteReqDto;
import com.multi.multi_semi.favorite.service.FavoriteService;
import com.multi.multi_semi.member.dto.MemberDto;
import com.multi.multi_semi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

//    @GetMapping
//    public ResponseEntity<List<FavoriteAllDto>> getFavorite() {
//        List<FavoriteAllDto> list = favoriteService.getAllFavorite();
//        return ResponseEntity.ok(list);
//    }

    @GetMapping("/favorites")
    public ResponseEntity<ResponseDto> selectFavoriteListWithPaging(@RequestParam(name = "offset", defaultValue = "1") String offset,@AuthenticationPrincipal CustomUser customUser) {


        log.info("[FavoriteController] selectFavoriteListWithPaging offset: {}", offset);
        log.info("[FavoriteController] selectFavoriteListWithPaging customerUser: {}", customUser);
        log.info("[FavoriteController] selectFavoriteListWithPaging email: {}", customUser.getEmail());


        SelectCriteria selectCriteria = getSelectCriteria(Integer.parseInt(offset), favoriteService.getFavoriteCount(customUser.getEmail()));

        ResponseDtoWithPaging responseDtoWithPaging = new ResponseDtoWithPaging(favoriteService.selectFavoriteListWithPaging(customUser.getEmail(),selectCriteria), selectCriteria);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "조회성공", responseDtoWithPaging));
    }

    private SelectCriteria getSelectCriteria(int offset, int totalCount) {

        int limit = 10;
        int buttonAmount = 10;

        return Pagenation.getSelectCriteria(offset, totalCount, limit, buttonAmount);
    }

    @PostMapping("/favorites/favorite")
    public ResponseEntity<ResponseDto> insertFavorite(@ModelAttribute FavoriteReqDto favoriteDto, @AuthenticationPrincipal CustomUser customUser) {
        favoriteDto.setMemEmail(customUser.getEmail());
        log.info("[FavoriteController] Insert favoriteDto: {}", favoriteDto);
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.CREATED, "favorite 등록 성공", favoriteService.insertFavorite(favoriteDto)));
    }

    @DeleteMapping("/favorites/{favoriteSeq}")
    public ResponseEntity<ResponseDto> deleteFavorite(@PathVariable("favoriteSeq") int favoriteSeq) {
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK, "favorite 삭제 성공", favoriteService.deleteFavorite(favoriteSeq)));
    }

}
