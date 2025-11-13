package com.multi.multi_semi.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.multi_semi.common.config.WeatherApiProperties;
import com.multi.multi_semi.weather.GridCoord;
import com.multi.multi_semi.weather.GridConverter;
import com.multi.multi_semi.weather.dto.WeatherDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiProperties props;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 같은 좌표(nx, ny)에 대해 30분 동안 재요청 방지용 캐시
    private final Map<String, CachedWeather> cache = new ConcurrentHashMap<>();

    /**
     * 위/경도로부터 현재 날씨(맑음/구름많음/흐림/비/눈)를 조회해서 WeatherDto로 반환
     */
    public WeatherDto getWeatherByLatLng(double lat, double lng) {

        // 1) 위경도 → 기상청 격자(nx, ny) 변환
        GridCoord grid = GridConverter.toGrid(lat, lng);
        String key = grid.getNx() + "_" + grid.getNy();

        // 2) 캐시 확인 (1시간 이내면 그대로 사용)
        CachedWeather cached = cache.get(key);
        if (cached != null && !cached.isExpired(60)) {
            return cached.getWeatherDto();
        }
        else
            System.out.println("New grid position:" + key);

        // 3) base_date / base_time 계산 (02시부터 3시간 간격)
        LocalDateTime nowKst = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        BaseDateTime base = calculateBaseDateTime(nowKst);

        // 4) 기상청 단기예보 API 호출
        String url = UriComponentsBuilder
                .fromHttpUrl(props.getBaseUrl()) // 예: http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst
                .queryParam("serviceKey", props.getServiceKey())
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", base.getBaseDate())
                .queryParam("base_time", base.getBaseTime())
                .queryParam("nx", grid.getNx())
                .queryParam("ny", grid.getNy())
                .build(true) // serviceKey에 인코딩 문자 있을 수 있어서
                .toUriString();

        String body = restTemplate.getForObject(url, String.class);

        // 5) 응답 JSON에서 "현재 시간에 가장 가까운 예보"의 SKY, PTY 코드 → WeatherDto 변환
        WeatherDto dto = parseWeather(body, nowKst);

        // 6) 캐시에 저장
        cache.put(key, new CachedWeather(dto));

        return dto;
    }

    /**
     * 기상청 JSON 응답에서:
     * - 오늘 날짜의
     * - targetTime(현재 시각)의 fcstTime(예: 15시 → "1500")
     * 에 해당하는 SKY/PTY를 우선 찾고,
     * 없으면 같은 날짜 중 아무 SKY/PTY 하나를 fallback으로 사용해서
     * 맑음/구름많음/흐림/비/눈 + emoji 로 매핑.
     */
    private WeatherDto parseWeather(String json, LocalDateTime targetTime) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (items.isMissingNode() || !items.isArray()) {
                throw new IllegalStateException("예보 item 배열이 없습니다.");
            }

            String targetDate = targetTime.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
            String targetFcstTime = String.format("%02d00", targetTime.getHour());   // HH00

            String skyCode = null;
            String ptyCode = null;

            // 1차: fcstDate == 오늘 && fcstTime == 현재시 정각 인 것만 우선 검색
            for (JsonNode item : items) {
                String fcstDate = item.path("fcstDate").asText();
                String fcstTime = item.path("fcstTime").asText();
                if (!targetDate.equals(fcstDate) || !targetFcstTime.equals(fcstTime)) {
                    continue;
                }

                String category = item.path("category").asText();
                String value = item.path("fcstValue").asText();

                if ("SKY".equals(category)) {
                    skyCode = value;
                } else if ("PTY".equals(category)) {
                    ptyCode = value;
                }
            }

            // 2차: 정확히 일치하는 시간이 없으면, 같은 날짜에서 임의의 SKY/PTY 하나를 fallback
            if (skyCode == null || ptyCode == null) {
                for (JsonNode item : items) {
                    String fcstDate = item.path("fcstDate").asText();
                    if (!targetDate.equals(fcstDate)) continue;

                    String category = item.path("category").asText();
                    String value = item.path("fcstValue").asText();

                    if ("SKY".equals(category) && skyCode == null) {
                        skyCode = value;
                    } else if ("PTY".equals(category) && ptyCode == null) {
                        ptyCode = value;
                    }
                }
            }

            // 못 찾았을 경우 기본값
            if (skyCode == null) skyCode = "1"; // 맑음
            if (ptyCode == null) ptyCode = "0"; // 강수 없음

            return mapCodeToDto(skyCode, ptyCode);

            // 여기까지 정상적으로 왔다면 dto 리턴
        } catch (Exception e) {
            e.printStackTrace();
            // 파싱 실패 시 기본값(맑음) 반환
            WeatherDto fallback = new WeatherDto();
            fallback.setType("SUNNY");
            fallback.setKorean("맑음");
            fallback.setEmoji("☀️");
            return fallback;
        }
    }

    /**
     * SKY / PTY 코드 → 맑음/구름많음/흐림/비/눈 + emoji 매핑
     * <p>
     * SKY: 1=맑음, 3=구름많음, 4=흐림
     * PTY: 0=없음, 1=비, 2=비/눈, 3=눈, 5=빗방울, 6=비/눈날림, 7=눈날림
     */
    private WeatherDto mapCodeToDto(String skyCode, String ptyCode) {
        WeatherDto dto = new WeatherDto();

        // 1) 강수 현상이 있으면 비/눈 우선
        if (!"0".equals(ptyCode)) {
            switch (ptyCode) {
                case "1": // 비
                case "2": // 비/눈
                case "5": // 빗방울
                case "6": // 비/눈날림
                    dto.setType("RAIN");
                    dto.setKorean("비");
                    dto.setEmoji("🌧️");
                    return dto;
                case "3": // 눈
                case "7": // 눈날림
                    dto.setType("SNOW");
                    dto.setKorean("눈");
                    dto.setEmoji("❄️");
                    return dto;
                default:
                    dto.setType("RAIN");
                    dto.setKorean("비");
                    dto.setEmoji("🌧️");
                    return dto;
            }
        }

        // 2) 강수 없으면 SKY 코드로 맑음/구름 많음/흐림
        switch (skyCode) {
            case "1": // 맑음
                dto.setType("SUNNY");
                dto.setKorean("맑음");
                dto.setEmoji("☀️");
                break;
            case "3": // 구름 많음
                dto.setType("CLOUDY");
                dto.setKorean("구름 많음");
                dto.setEmoji("⛅");
                break;
            case "4": // 흐림
                dto.setType("OVERCAST");
                dto.setKorean("흐림");
                dto.setEmoji("☁️");
                break;
            default:
                dto.setType("SUNNY");
                dto.setKorean("맑음");
                dto.setEmoji("☀️");
        }

        return dto;
    }

    /**
     * 02시부터 3시간 간격(02,05,08,11,14,17,20,23)으로 base_time 계산
     * - 지금 시각보다 작거나 같은 값 중 가장 큰 base_time 선택
     * - 새벽 0~1시는 전날 23시 사용
     */
    private BaseDateTime calculateBaseDateTime(LocalDateTime nowKst) {
        int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};

        int hour = nowKst.getHour();
        LocalDateTime baseDateTime;

        int chosenHour = 2;

        // 현재 시각보다 작거나 같은 baseHours 중 가장 큰 값 선택
        for (int h : baseHours) {
            if (hour >= h) {
                chosenHour = h;
            }
        }

        // 새벽 0~1시는 전날 23시 예보 사용
        if (hour < 2) {
            baseDateTime = nowKst.minusDays(1).withHour(23);
        } else {
            baseDateTime = nowKst.withHour(chosenHour);
        }

        String baseDate = baseDateTime.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        String baseTime = String.format("%02d00", baseDateTime.getHour());       // HH00

        return new BaseDateTime(baseDate, baseTime);
    }

    @Getter
    private static class BaseDateTime {
        private final String baseDate; // yyyyMMdd
        private final String baseTime; // HHmm

        public BaseDateTime(String baseDate, String baseTime) {
            this.baseDate = baseDate;
            this.baseTime = baseTime;
        }
    }

    @Getter
    private static class CachedWeather {
        private final WeatherDto weatherDto;
        private final LocalDateTime savedAt;

        public CachedWeather(WeatherDto weatherDto) {
            this.weatherDto = weatherDto;
            this.savedAt = LocalDateTime.now();
        }

        public boolean isExpired(int minutes) {
            return savedAt.plusMinutes(minutes).isBefore(LocalDateTime.now());
        }
    }
}