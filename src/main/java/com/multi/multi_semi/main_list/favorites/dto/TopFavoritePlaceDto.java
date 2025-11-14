package com.multi.multi_semi.main_list.favorites.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TopFavoritePlaceDto {

    // 관광지 PK
    private Long placeNo;

    // 관광지 이름
    private String placeTitle;

    // 즐겨찾기 개수
    private Long favoriteCount;

    // 대표 이미지 (place.first_img)
    private String imgUrl;
}