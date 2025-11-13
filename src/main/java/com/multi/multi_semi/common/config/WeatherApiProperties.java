package com.multi.multi_semi.common.config;

import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

// WeatherApiProperties.java
@Configuration
@ConfigurationProperties(prefix = "weather.api")
@Getter
@Setter
public class WeatherApiProperties {
    private String baseUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private String serviceKey = "840c17bf5c4963f579cbfb30d6fb142cc96e33109a8b310053562fdf7eeba455";
}