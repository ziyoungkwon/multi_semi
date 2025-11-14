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

    // ✅ application.yml의 경로 키에 맞게 수정
    @Value("${image.image-dir}")
    private String ADD_RESOURCE_LOCATION;


    @Value("${image.add-resource-handler}")
    private String ADD_RESOURCE_HANDLER;



    @Value("${image.image-url}")
    private String IMAGE_URL; // ← 필요 시 다른 클래스에서도 이 값 사용 가능


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
        // 경로 자동 처리
        String resourcePath = ADD_RESOURCE_LOCATION.startsWith("file:")
                ? ADD_RESOURCE_LOCATION
                : "file:" + ADD_RESOURCE_LOCATION;

        // --- 1) 리뷰 이미지(일반 이미지) 매핑 추가 ---
        registry.addResourceHandler(ADD_RESOURCE_HANDLER)
                .addResourceLocations(resourcePath);

        // --- 2) ai 이미지 매핑 ---
        registry.addResourceHandler(aiResourceHandler)
                .addResourceLocations(aiResourceLocation);

        System.out.println("[STATIC RESOURCE MAPPING]");
        System.out.println("Handler  : " + ADD_RESOURCE_HANDLER);
        System.out.println("Location : " + resourcePath);

        System.out.println("[STATIC RESOURCE MAPPING 2: AI Images]");
        System.out.println("Handler  : " + aiResourceHandler);
        System.out.println("Location : " + aiResourceLocation);
    }

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
