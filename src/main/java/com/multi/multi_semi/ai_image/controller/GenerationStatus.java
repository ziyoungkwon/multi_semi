package com.multi.multi_semi.ai_image.controller;

/**
 * 작업의 현재 상태를 나타내는 DTO (Data Transfer Object)
 *
 * @param status   "PENDING", "SUCCESS", "FAILED"
 * @param imageUrl 성공 시 이미지 URL
 * @param error    실패 시 에러 메시지
 */
public record GenerationStatus(String status, String imageUrl, String error) {

    public static GenerationStatus pending() {
        return new GenerationStatus("PENDING", null, null);
    }

    public static GenerationStatus success(String imageUrl) {
        return new GenerationStatus("SUCCESS", imageUrl, null);
    }

    public static GenerationStatus failed(String error) {
        return new GenerationStatus("FAILED", null, error);
    }
}
