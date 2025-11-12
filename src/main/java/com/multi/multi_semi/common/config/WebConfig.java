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
    @Value("${image.add-resource-locations}")
    private String ADD_RESOURCE_LOCATION;

    @Value("${image.add-resource-handler}")
    private String ADD_RESOURCE_HANDLER;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(ADD_RESOURCE_HANDLER) //    // 요청 URL 패턴을 지정 (예: /productimgs/**)
                .addResourceLocations("file://" + ADD_RESOURCE_LOCATION); //    // 실제 파일 시스템의 경로를 지정 (예: file:///productimgs/)
    }

    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

}