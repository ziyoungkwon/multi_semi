package com.multi.multi_semi.review.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReviewReqDto {

    private int no;
    private String title;
    private String content;
    private int rate;
    private int writerNo;
    private int placeNo;
    private int modifiedBy;
    private String imgUrl;

}
