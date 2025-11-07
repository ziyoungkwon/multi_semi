package com.multi.multi_semi.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteAllDto {
    private long seq;
    private String userId;
    private String no;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
