package com.multi.multi_semi.common.jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// /refresh 응답 전용 DTO (RT가 빠져있음)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponseDto {
    private String accessToken;
}
