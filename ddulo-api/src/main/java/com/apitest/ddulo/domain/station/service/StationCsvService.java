package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.metadata.domain.ApiMetadata;
import com.apitest.ddulo.domain.metadata.repository.ApiMetadataRepository;
import com.apitest.ddulo.domain.metadata.service.ApiMetadataService;
import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import com.apitest.ddulo.global.exception.CustomException;
import com.apitest.ddulo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationCsvService {

    private static String API_NAME;
    private final StationRepository stationRepository;
    private final ApiMetadataService apiMetadataService;
    private final ApiMetadataRepository apiMetadataRepository;

    @Transactional
    public void updateStationCoordinates(String filePath){
        API_NAME = filePath;
        if (!apiMetadataService.isApiCallNeeded(API_NAME)) {
            log.info("Station 좌표 데이터가 이미 존재하므로, 다음으로 넘어갑니다.");
            return;
        }
        log.info("Station 좌표 데이터를 찾을 수 없으므로, 동기화를 실행합니다.");

        csvUpdate(filePath);

        ApiMetadata metadata = apiMetadataRepository.findByApiName(API_NAME)
                .orElseGet(() -> ApiMetadata.builder()
                        .apiName(API_NAME)
                        .updateIntervalDay(null) // null : 최초 1회만, N : N일마다 업데이트
                        .build());

        metadata.markUpdated(); // 현재 시간 찍기
        apiMetadataRepository.save(metadata);
    }

    private void csvUpdate(String filePath) {
        log.info("CSV 위치 데이터 병합 시작: {}", filePath);

        // 기존 DB의 모든 역 데이터를 가져와서 Map으로 변환
        // ex: Key: "9호선_언주역", Value: Station 객체
        Map<String, Station> stationMap = stationRepository.findAll().stream()
                .collect(Collectors.toMap(
                        s -> generateKey(s.getLineName(), s.getStationName()),
                        s -> s,
                        (existing, replacement) -> existing // 중복 키 발생 시 기존 값 유지
                ));

        ClassPathResource resource = new ClassPathResource(filePath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> lines = br.lines().skip(1).collect(Collectors.toList()); // 헤더 스킵

            for (String line : lines) {
                String[] columns = line.split(",");
                if (columns.length < 6) continue;

                String lineName = normalizeLineName(columns[1]); // "9" -> "9호선"
                String stationName = normalizeStationName(columns[3]); // "언주" -> "언주역"
                Double lat = Double.parseDouble(columns[4]);
                Double lon = Double.parseDouble(columns[5]);

                String key = generateKey(lineName, stationName);
                Station targetStation = stationMap.get(key);

                if (targetStation != null) {
                    // Dirty Checking을 통한 업데이트
                    targetStation.updateCoordinates(lat, lon);
                }
            }
            log.info("지하철역 위치 정보 병합 완료.");
        } catch (Exception e) {
            log.error("CSV 병합 중 오류 발생: ", e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private String generateKey(String line, String name) {
        return line + "_" + name;
    }

    private String normalizeLineName(String line) {
        if (line == null || line.isBlank()) {
            return line;
        }
        String trimmedLine = line.trim();

        if (trimmedLine.matches("^[0-9]+$")) {
            return trimmedLine + "호선";
        }

        return trimmedLine;
    }

    private String normalizeStationName(String name) {
        return name.endsWith("역") ? name : name + "역";
    }
}
