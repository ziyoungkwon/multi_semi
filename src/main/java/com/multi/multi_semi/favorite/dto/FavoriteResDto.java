package com.multi.multi_semi.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResDto {
    private long favoriteSeq;
    private long placeNo;
    private String district;
    private String title;
    private String description;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String imageUrl;
}
