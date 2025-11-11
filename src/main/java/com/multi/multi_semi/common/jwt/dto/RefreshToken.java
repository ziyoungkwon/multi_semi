package com.multi.multi_semi.common.jwt.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RefreshToken {
    private Long no;
    private String email;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

}
