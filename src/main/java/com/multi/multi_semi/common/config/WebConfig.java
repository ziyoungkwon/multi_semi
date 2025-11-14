package com.multi.multi_semi.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // --- 1. 기존 설정 값 (productimgs) ---
    // [수정] 변수명을 좀 더 명확하게 변경 (선택 사항)
    @Value("${image.add-resource-locations}")
    private String productResourceLocation;

    @Value("${image.add-resource-handler}")
    private String productResourceHandler;

    @Value("${image.image-url}")
    private String IMAGE_URL;

    // --- 2. [추가] 새 설정 값 (ai_img) ---
    @Value("${file.resource-locations}")
    private String aiResourceLocation;

    @Value("${file.resource-handler}")
    private String aiResourceHandler;


    // --- (기존 ObjectMapper 로직 - 수정 없음) ---
    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // --- 3. [수정] addResourceHandlers 메서드 ---
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // --- (A) 기존 Product 이미지 핸들러 등록 (기존 로직) ---
        String productPath = productResourceLocation.startsWith("file:")
                ? productResourceLocation
                : "file:" + productResourceLocation;

        registry.addResourceHandler(productResourceHandler)
                .addResourceLocations(productPath);


        // --- (B) [추가] 새 AI 이미지 핸들러 등록 ---
        // 'file.resource-locations'는 yml에서 이미 'file:' 접두사를 포함하므로
        // 별도 검사 없이 바로 사용합니다.
        registry.addResourceHandler(aiResourceHandler)
                .addResourceLocations(aiResourceLocation);


        // --- (C) [수정] 로그 출력 (두 핸들러 모두 표시) ---
        System.out.println("[STATIC RESOURCE MAPPING 1: Products]");
        System.out.println("  Handler  : " + productResourceHandler);
        System.out.println("  Location : " + productPath);
        System.out.println("  Image URL: " + IMAGE_URL);

        System.out.println("[STATIC RESOURCE MAPPING 2: AI Images]");
        System.out.println("  Handler  : " + aiResourceHandler);
        System.out.println("  Location : " + aiResourceLocation);
    }

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
