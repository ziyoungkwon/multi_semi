package com.multi.multi_semi.main_list.rating.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TopRatedPlaceDto {

    // 관광지 PK
    private Long placeNo;

    // 관광지 이름
    private String placeTitle;

    // rate 합계 / 개수 (DB에서 가져옴)
    private Long rateSum;
    private Long rateCount;

    // 대표 이미지 (리뷰 이미지 중 하나)
    private String imgUrl;

    // Service에서 계산해서 세팅해 줄 평균 평점
    private Double avgRate;
}
