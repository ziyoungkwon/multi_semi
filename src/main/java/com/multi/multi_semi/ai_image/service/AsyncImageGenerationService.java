package com.multi.multi_semi.ai_image.service;

import com.multi.multi_semi.ai_image.controller.GenerationStatus;
import com.multi.multi_semi.ai_image.dao.AiImgMapper;
import com.multi.multi_semi.ai_image.dto.AiImgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsyncImageGenerationService {

    // 1. 사용자의 원본 OpenAIService (오래 걸리는 작업)
    private final OpenAIService openAIService;

    // 2. AppConfig에서 Bean으로 등록한 결과 저장소
    private final Map<String, GenerationStatus> taskResults;

    // 3. [추가] DB 저장을 위한 Mapper
    private final AiImgMapper aiImgMapper;

    // 4. [추가] yml에서 파일 저장 경로 주입
    // 예: "C:/workspace/org_ai_img/"
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 5. [추가] yml에서 웹 핸들러 경로(접두사) 주입
    // "${file.resource-handler}" (예: "/images/ai/**") 에서 "/**" 를 제거
    // 결과: "/images/ai"
    @Value("#{'${file.resource-handler}'.replace('/**', '')}")
    private String webUrlPrefix;

    public List<AiImgDto> getImagesForUser(String email) {
        return aiImgMapper.findByEmail(email);
    }


    /**
     * [핵심] @Async 어노테이션
     * 이 메서드는 별도의 백그라운드 스레드에서 실행됩니다.
     *
     * [수정됨]
     * 1. byte[] 배열을 받습니다. (NoSuchFileException 방지)
     * 2. 작업 완료 시 파일 저장 및 DB Insert 로직 추가
     */
    @Async
    public void generateImageAsync(String taskId, byte[] image1Bytes, byte[] image2Bytes, String prompt, String email) {
        try {
            // [1. 기존 작업] OpenAI로부터 이미지 생성 (이 URL은 OpenAI의 임시 URL)
            // (오래 걸리는 작업)
            String openAiImageUrl = openAIService.processFusion(image1Bytes, image2Bytes, prompt, email);

            // --- [2. 신규 작업] 파일 저장 및 DB 저장 ---

            // (A) 고유한 파일명 생성 (예: aaaaa-bbbb-cccc-dddd.png)
            String newFileName = UUID.randomUUID().toString() + ".png";

            // (B) 실제 저장될 전체 경로 (예: C:/workspace/org_ai_img/aaaaa-bbbb-cccc-dddd.png)
            Path localFilePath = Paths.get(uploadDir + newFileName);

            // (C) (중요) 디렉터리가 없으면 생성 (C:/workspace/org_ai_img)
            Files.createDirectories(localFilePath.getParent());

            // (D) OpenAI URL(openAiImageUrl)에서 InputStream을 열어 이미지를 다운로드
            // (try-with-resources 구문으로 InputStream 자동 close)
            try (InputStream in = new URL(openAiImageUrl).openStream()) {
                // (E) 다운로드한 이미지를 (B)의 로컬 경로에 복사(저장)
                // 이미 파일이 있다면 덮어씁니다.
                Files.copy(in, localFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // (F) 브라우저(클라이언트)가 접근할 수 있는 최종 URL 생성
            // 예: "/images/ai" + "/" + "aaaaa-bbbb-cccc-dddd.png"
            String webAccessibleUrl = webUrlPrefix + "/" + newFileName;

            // (G) DTO 생성 및 DB 저장
            // (AiImgDTO는 memEmail과 orgUrl만 받는 record 또는 클래스여야 함)
            AiImgDto imgDTO = new AiImgDto(email, webAccessibleUrl);
            aiImgMapper.insertAiImg(imgDTO);

            // [3. 수정된 작업]
            // 작업 성공 시, OpenAI URL이 아닌 *우리 서버의 URL*을 반환
            taskResults.put(taskId, GenerationStatus.success(webAccessibleUrl));

        } catch (Exception e) {
            // 작업 실패 시 (OpenAI 실패, 파일 다운로드 실패, DB 저장 실패 등)
            // 결과 저장소에 "FAILED" 상태와 에러 메시지 저장
            taskResults.put(taskId, GenerationStatus.failed(e.getMessage()));
            e.printStackTrace(); // 서버 로그에는 전체 에러 출력
        }
    }
}