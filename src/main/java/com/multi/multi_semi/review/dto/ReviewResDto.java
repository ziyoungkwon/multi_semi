package com.multi.multi_semi.review.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReviewResDto {

    private Long no;
    private String title;
    private String content;
    private int rate;
    private String writerEmail;
    private String writer;
    private String placeTitle;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedPerson;
}
