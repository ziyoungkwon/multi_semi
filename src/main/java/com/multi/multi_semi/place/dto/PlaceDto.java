package com.multi.multi_semi.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {
    private long no;
    private String district;
    private String title;
    private String description;
    private String address;
    private String phone;
    private BigDecimal lat;
    private BigDecimal lng;
    private String imageUrl;
    private double avgRate;
}

