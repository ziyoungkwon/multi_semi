package com.multi.multi_semi.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResDto {
    private Long reviewNo;
    private String title;
    private String content;
    private int rate;
    private String email;
}

