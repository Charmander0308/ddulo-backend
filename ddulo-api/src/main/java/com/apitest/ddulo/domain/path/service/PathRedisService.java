package com.apitest.ddulo.domain.path.service;

import com.apitest.ddulo.domain.path.dto.external.redis.PathFullData;
import com.apitest.ddulo.domain.path.dto.external.redis.PathPredictionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.apitest.ddulo.global.utils.DateUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 전체 경로 및 상세 정보 조회 (path:full)
    public PathFullData getFullPath(String startId, String endId) {
        List<Object> rawDataList = getRedisDataByTimeKeys("path:full", startId, endId);

        return parseFirstValid(rawDataList, PathFullData.class);
    }

    // 경로 예측 요약 정보 조회 (path:pred)

    public PathPredictionData getPathPrediction(String startId, String endId) {
        List<Object> rawDataList = getRedisDataByTimeKeys("path:pred", startId, endId);

        return mergePredictionData(rawDataList);
    }

    /**
     * Redis에서 현재 시간대와 다음 시간대 데이터를 조회하는 공통 메서드
     *
     * @param prefix Redis 키 접두사 (예: "path:full", "path:pred")
     * @param startId 출발역 ID
     * @param endId 도착역 ID
     * @return 조회된 데이터 리스트
     */
    private List<Object> getRedisDataByTimeKeys(String prefix, String startId, String endId) {
        String todayKey = convertDateToDayKey(String.valueOf(LocalDate.now()));

        String currentKey = buildRedisKey(prefix, startId, endId, todayKey, getCurrentTimeKey());
        String nextKey = buildRedisKey(prefix, startId, endId, todayKey, getNextTimeKey());

        return redisTemplate.opsForValue().multiGet(Arrays.asList(currentKey, nextKey));
    }

    /**
     * Redis 키를 생성하는 헬퍼 메서드
     *
     * @param prefix 키 접두사
     * @param startId 출발역 ID
     * @param endId 도착역 ID
     * @param dayKey 요일 키
     * @param timeKey 시간 키
     * @return 완성된 Redis 키
     */
    private String buildRedisKey(String prefix, String startId, String endId,
                                 String dayKey, String timeKey) {
        return String.format("%s:%s:%s:%s:%s", prefix, startId, endId, dayKey, timeKey);
    }

    // PathPredictionData 병합 로직
    private PathPredictionData mergePredictionData(List<Object> rawDataList) {
        if (rawDataList == null || rawDataList.isEmpty()) return null;

        PathPredictionData mergedData = null;

        for (Object rawJson : rawDataList) {
            if (rawJson == null) continue;

            try {
                PathPredictionData data = objectMapper.readValue(rawJson.toString(), PathPredictionData.class);

                if (mergedData == null) {
                    mergedData = data; // 첫 번째 데이터(현재 시간대)를 기준으로 잡음
                } else {
                    // 두 번째 데이터(다음 시간대)의 Schedule 리스트를 첫 번째 데이터 뒤에 붙임
                    if (data.getResults() != null && !data.getResults().isEmpty()) {
                        // null 체크 등 방어 로직 필요
                        var existingSchedule = mergedData.getResults().get(0).getSchedule();
                        var nextSchedule = data.getResults().get(0).getSchedule();

                        if(existingSchedule != null && nextSchedule != null) {
                            existingSchedule.addAll(nextSchedule);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Redis 데이터 파싱 실패", e);
            }
        }
        return mergedData;

    }

    // Redis조회 결과 리스트를 가져와서 유효한 데이터를 파싱하는 공통 로직
    private <T> T parseFirstValid(List<Object> rawDataList, Class<T> clazz) {
        if (rawDataList == null) return null;
        for (Object raw : rawDataList) {
            if (raw != null) {
                try {
                    return objectMapper.readValue(raw.toString(), clazz);
                } catch (Exception e) {
                    log.error("파싱 에러", e);
                }
            }
        }
        return null;
    }

    // Redis에서 가져와서 DTO로 변환하는 공통 로직
    @Deprecated
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
