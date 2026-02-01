package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.metadata.domain.ApiMetadata;
import com.apitest.ddulo.domain.metadata.repository.ApiMetadataRepository;
import com.apitest.ddulo.domain.metadata.service.ApiMetadataService;
import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationDataSyncService {

    private static final String API_NAME = "PUZZLE_STATION_META";

    private final StationRepository stationRepository;
    private final ApiMetadataRepository apiMetadataRepository;
    private final ApiMetadataService apiMetadataService;

    @Transactional
    public void syncStationsIfNeeded() {

        // 가져올 필요 있는지 판단
        if (!apiMetadataService.isApiCallNeeded(API_NAME)) {
            log.info("Station 데이터가 이미 존재하므로, 다음으로 넘어갑니다.");
            return;
        }
        log.info("Station 데이터를 찾을 수 없으므로, 동기화를 실행합니다.");

        // 외부 API 호출
//        syncStations();

        // CSV 데이터 로드
        loadStationCsvData();

        //성공 후 메타데이터 갱신 (없으면 생성, 있으면 업데이트)
        ApiMetadata metadata = apiMetadataRepository.findByApiName(API_NAME)
                .orElseGet(() -> ApiMetadata.builder()
                        .apiName(API_NAME)
                        .updateIntervalDay(null) // null : 최초 1회만, N : N일마다 업데이트
                        .build());

        metadata.markUpdated(); // 현재 시간 찍기
        apiMetadataRepository.save(metadata);
    }

    private void loadStationCsvData() {
        ClassPathResource resource = new ClassPathResource("data/station.csv");

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            // 헤더 포함 모든 라인 읽기
            List<String[]> allRows = csvReader.readAll();
            List<Station> stationList = new ArrayList<>();

            // 파싱 (i = 1 부터 시작해서 헤더 스킵)
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);

                // 빈 줄 방지
                if (row.length < 7) continue;

                // CSV 컬럼 인덱스 매핑
                // 0:id, 1:created_at, 2:lat, 3:line, 4:lon, 5:code, 6:name, 7:updated_at

                String lineName = row[3];      // "1호선"
                String stationCode = row[5];   // "100-1" (String이어야 함!)
                String stationName = row[6];   // "소요산역"

                // 위도, 경도 파싱 (빈 값이면 0.0 처리 등 방어 로직 추가 가능)
                double lat = parseDoubleOrDefault(row[2]);
                double lon = parseDoubleOrDefault(row[4]);

                // 엔티티 생성
                Station station = Station.builder()
                        .lineName(lineName)
                        .stationCode(stationCode)
                        .stationName(stationName)
                        .latitude(lat)
                        .longitude(lon)
                        .build();

                stationList.add(station);
            }

            // DB 저장
            stationRepository.saveAll(stationList);
            log.info("[DataInit] 총 {}건의 Station 데이터 저장 완료", stationList.size());

        } catch (Exception e) {
            log.error("[DataInit] CSV 초기화 중 오류 발생", e);
            // throw new RuntimeException("데이터 초기화 실패");
        }
    }

    private double parseDoubleOrDefault(String value) {
        // null이거나, 빈 문자열이거나, 공백만 있는 경우
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }

        try {
            // 정상 파싱 시도
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            // 숫자가 아닌 이상한 문자(예: "unknown")가 들어왔을 때도 0.0 처리
            return 0.0;
        }
    }


//    @Transactional
//    public void syncStations() {
//        int offset = 0;
//        int limit = 100;
//
//        while (true) {
//            log.info("Fetching station info offset: {}, limit: {}", offset, limit);
//            PuzzleStationResponseDto response =
//                    stationApiClient.fetchStations(offset, limit);
//
//            if (response == null || response.getContents() == null) {
//                log.error("Failed to fetch station info");
//                break;
//            }
//
//            for (StationItemDto item : response.getContents()) {
//
//                if (!stationRepository.existsByStationCode(item.getStationCode())) {
//                    Station station = Station.builder()
//                            .stationCode(item.getStationCode())
//                            .stationName(item.getStationName())
//                            .lineName(item.getSubwayLine())
//                            .build();
//
//                    stationRepository.save(station);
//                }
//            }
//
//            offset += limit;
//            if (response.getStatus() == null || offset >= response.getStatus().getTotalCount()) {
//                break;
//            }
//        }
//        log.info("Station info sync completed.");
//    }
}
