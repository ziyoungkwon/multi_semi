package com.multi.multi_semi.ai_image.dto;

// Java 17+ 이상이면 record 사용을 권장합니다.
// (자동으로 생성자, getter, equals, hashCode, toString 생성)
public record AiImgDto(
        String memEmail,
        String orgUrl
        // thumUrl은 쿼리에서 orgUrl을 사용하므로 DTO에는 필요 없습니다.
) {
    // 만약 record를 사용하지 않고 클래스를 사용한다면
    // private String memEmail;
    // private String orgUrl;
    // (Lombok @Data 또는 getter/setter/생성자)
}
