package com.multi.multi_semi.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
