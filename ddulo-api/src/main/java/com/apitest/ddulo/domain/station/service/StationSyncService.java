package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.metadata.domain.ApiMetadata;
import com.apitest.ddulo.domain.metadata.repository.ApiMetadataRepository;
import com.apitest.ddulo.domain.metadata.service.ApiMetadataService;
import com.apitest.ddulo.domain.station.client.StationApiClient;
import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.dto.PuzzleStationResponseDto;
import com.apitest.ddulo.domain.station.dto.StationItemDto;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationSyncService {

    private static final String API_NAME = "PUZZLE_STATION_META";

    private final StationApiClient stationApiClient;
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
        syncStations();

        //성공 후 메타데이터 갱신 (없으면 생성, 있으면 업데이트)
        ApiMetadata metadata = apiMetadataRepository.findByApiName(API_NAME)
                .orElseGet(() -> ApiMetadata.builder()
                        .apiName(API_NAME)
                        .updateIntervalDay(null) // null : 최초 1회만, N : N일마다 업데이트
                        .build());

        metadata.markUpdated(); // 현재 시간 찍기
        apiMetadataRepository.save(metadata);
    }

    @Transactional
    public void syncStations() {
        int offset = 0;
        int limit = 100;

        while (true) {
            log.info("Fetching station info offset: {}, limit: {}", offset, limit);
            PuzzleStationResponseDto response =
                    stationApiClient.fetchStations(offset, limit);

            if (response == null || response.getContents() == null) {
                log.error("Failed to fetch station info");
                break;
            }

            for (StationItemDto item : response.getContents()) {

                if (!stationRepository.existsByStationCode(item.getStationCode())) {
                    Station station = Station.builder()
                            .stationCode(item.getStationCode())
                            .stationName(item.getStationName())
                            .lineName(item.getSubwayLine())
                            .build();

                    stationRepository.save(station);
                }
            }

            offset += limit;
            if (response.getStatus() == null || offset >= response.getStatus().getTotalCount()) {
                break;
            }
        }
        log.info("Station info sync completed.");
    }
}
