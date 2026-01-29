package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.station.dto.StationDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public StationDetailResponse getStationDetail(Long stationId) {
        // Redis Key 생성: stat:near:{stationId}:{요일}:{시간}
        String redisKey = String.format(
                "stat:near:%d:%s:%s",
                stationId,
                convertDateToDayKey(String.valueOf(LocalDate.now())),
                getCurrentTimeKey()
        );

        Object rawData = redisTemplate.opsForValue().get(redisKey);

        if (rawData == null) return null;

        try {
            return objectMapper.readValue(rawData.toString(), StationDetailResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("데이터 변환 실패");
        }
    }

    // Util: 2026-01-28 -> WED 로 변환하는 메서드
    private String convertDateToDayKey(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return date.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase();
    }

    // Util: 10분 간격으로 내림 처리하는 메서드
    private String getCurrentTimeKey() {
        LocalTime now = LocalTime.now();

        // 10분 단위로 내림 (Floor) 계산
        // ex: 47분 -> 40분, 03분 -> 00분
        int minute = now.getMinute();
        int roundedMinute = (minute / 10) * 10;

        // 시간을 다시 설정 (초는 00으로)
        LocalTime targetTime = now.withMinute(roundedMinute).withSecond(0).withNano(0);

        // Redis 키 형식(HHmm)으로 변환
        // 예: 14:30:00 -> "1430"
        return targetTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }
}
