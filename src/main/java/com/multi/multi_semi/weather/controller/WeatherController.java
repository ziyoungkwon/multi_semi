package com.multi.multi_semi.weather.controller;

import com.multi.multi_semi.weather.dto.*;
import com.multi.multi_semi.weather.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// WeatherController.java
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<?> getWeather(@RequestParam double lat,
                                        @RequestParam double lng) {

        WeatherDto dto = weatherService.getWeatherByLatLng(lat, lng);

        // 프론트에서 쓰기 쉬운 형태로 래핑 (status + data)
        Map<String, Object> body = new HashMap<>();
        body.put("status", 200);
        body.put("data", dto);

        return ResponseEntity.ok(body);
    }
}

