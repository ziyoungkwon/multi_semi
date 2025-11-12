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
    @Value("${image.add-resource-locations}")
    private String ADD_RESOURCE_LOCATION;

    @Value("${image.add-resource-handler}")
    private String ADD_RESOURCE_HANDLER;

    @Value("${image.url}")
    private String IMAGE_URL; // ← 필요 시 다른 클래스에서도 이 값 사용 가능

    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ yml에 이미 file:/... 로 시작하므로 “file:”을 중복 추가하지 않게 체크
        String resourcePath = ADD_RESOURCE_LOCATION.startsWith("file:")
                ? ADD_RESOURCE_LOCATION
                : "file:" + ADD_RESOURCE_LOCATION;

        registry.addResourceHandler(ADD_RESOURCE_HANDLER)
                .addResourceLocations(resourcePath);

        System.out.println("[STATIC RESOURCE MAPPING]");
        System.out.println("Handler  : " + ADD_RESOURCE_HANDLER);
        System.out.println("Location : " + resourcePath);
        System.out.println("Image URL: " + IMAGE_URL);
    }

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
