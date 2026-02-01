package com.apitest.ddulo.domain.path.service;

import com.apitest.ddulo.domain.path.dto.response.PathFullResponse;
import com.apitest.ddulo.domain.path.dto.response.PathPredictionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.apitest.ddulo.global.utils.DateUtils.convertDateToDayKey;
import static com.apitest.ddulo.global.utils.DateUtils.getCurrentTimeKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 전체 경로 및 상세 정보 조회 (path:full)
    public PathFullResponse getFullPath(String startId, String endId) {
        // Redis Key 생성: path:full:{start}:{end}:{요일}:{시간}
        String redisKey = String.format(
                "path:full:%s:%s:%s:%s",
                startId,
                endId,
                convertDateToDayKey(String.valueOf(LocalDate.now())),
                getCurrentTimeKey() //버림 메서드
        );

        return fetchData(redisKey, PathFullResponse.class);
    }

    // 경로 예측 요약 정보 조회 (path:pred)
    public PathPredictionResponse getPathPrediction(String startId, String endId) {
        // Redis Key 생성: path:pred:{start}:{end}:{요일}:{시간}
        String redisKey = String.format(
                "path:pred:%s:%s:%s:%s",
                startId,
                endId,
                convertDateToDayKey(String.valueOf(LocalDate.now())),
                getCurrentTimeKey() //버림 메서드
        );

        return fetchData(redisKey, PathPredictionResponse.class);
    }

    // Redis에서 가져와서 DTO로 변환하는 공통 로직
    private <T> T fetchData(String key, Class<T> classType) {
        Object rawData = redisTemplate.opsForValue().get(key);

        if (rawData == null) {
            return null; // 또는 빈 객체 리턴, 혹은 예외 처리
        }

        try {
            // Redis에 저장된 게 String(JSON)이라면 파싱
            return objectMapper.readValue(rawData.toString(), classType);
        } catch (Exception e) {
            throw new RuntimeException("데이터 변환 중 오류 발생");
        }
    }

}
