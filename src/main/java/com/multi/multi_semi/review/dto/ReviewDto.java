package com.multi.multi_semi.review.dto;

import lombok.*;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReviewDto {

    private Long no;
    private String title;
    private String content;
    private int rate;
    private String writerEmail;
    private int placeNo;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int updaterNo;
}
