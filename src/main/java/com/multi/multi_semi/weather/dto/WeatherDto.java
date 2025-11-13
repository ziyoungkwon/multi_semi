package com.multi.multi_semi.weather.dto;
import lombok.*;

// WeatherDto.java (대략 이런 느낌이면 됨)
@Getter
@Setter
public class WeatherDto {
    private String type;     // SUNNY, CLOUDY, OVERCAST, RAIN, SNOW
    private String korean;   // 맑음, 구름 많음, 흐림, 비, 눈
    private String iconUrl;  // /img/weather/sunny.png 같은 경로
}