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

import static com.apitest.ddulo.global.utils.DateUtils.convertDateToDayKey;
import static com.apitest.ddulo.global.utils.DateUtils.getCurrentTimeKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // Redis에서 역 상세정보(양 방향으로 인접한 열차 상태 3개씩 조회) 조회
    public StationDetailResponse getStationDetail(Long stationId) {
        // Redis Key 생성: stat:near:{stationId}:{요일}:{시간}
        String redisKey = String.format(
                "stat:near:%d:%s:%s",
                stationId,
                convertDateToDayKey(String.valueOf(LocalDate.now())),
                getCurrentTimeKey()
        );

        Object rawData = redisTemplate.opsForValue().get(redisKey);

        if (rawData == null) return null;   // 예외처리 로직 추가하기

        try {
            return objectMapper.readValue(rawData.toString(), StationDetailResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("데이터 변환 실패");    // 커스텀 예외처리 로직 추가하기
        }
    }

}
