package com.multi.multi_semi.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private String writerEmail;
    private int placeNo;
    private String modifiedBy;
    private String imgUrl; // DB 저장용 파일명

    // ✅ 추가: 업로드된 파일을 직접 받기 위한 필드
    private MultipartFile imgFile;

}
