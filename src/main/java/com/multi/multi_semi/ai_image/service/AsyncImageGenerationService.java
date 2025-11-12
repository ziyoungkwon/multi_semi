package com.multi.multi_semi.ai_image.service;

import com.multi.multi_semi.ai_image.controller.GenerationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AsyncImageGenerationService {

    // 1. 사용자의 원본 OpenAIService (오래 걸리는 작업)
    private final OpenAIService openAIService;

    // 2. AppConfig에서 Bean으로 등록한 결과 저장소
    private final Map<String, GenerationStatus> taskResults;

    /**
     * [핵심] @Async 어노테이션
     * 이 메서드는 별도의 백그라운드 스레드에서 실행됩니다.
     * 따라서 이 메서드를 호출한 컨트롤러는 즉시 리턴됩니다.
     *
     * [수정됨]
     * 컨트롤러에서 미리 읽어둔 byte[] 배열을 받습니다.
     * 이제 임시 파일(.tmp)에 접근할 일이 없으므로 NoSuchFileException이 발생하지 않습니다.
     */
    @Async
    public void generateImageAsync(String taskId, byte[] image1Bytes, byte[] image2Bytes, String prompt) {
        try {
            // [오래 걸리는 작업]
            // 수정된 OpenAIService의 processFusion 메서드를 호출합니다.
            String imageUrl = openAIService.processFusion(image1Bytes, image2Bytes, prompt);

            // 작업 성공 시, 결과 저장소에 "SUCCESS" 상태와 URL 저장
            taskResults.put(taskId, GenerationStatus.success(imageUrl));

        } catch (Exception e) {
            // 작업 실패 시, 결과 저장소에 "FAILED" 상태와 에러 메시지 저장
            taskResults.put(taskId, GenerationStatus.failed(e.getMessage()));
            e.printStackTrace(); // 서버 로그에는 전체 에러 출력
        }
    }
}