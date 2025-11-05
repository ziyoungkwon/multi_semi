package com.multi.multi_semi.common.jwt.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RefreshToken {
    private Long id;
    private String email;
    private String refreshToken;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

}
