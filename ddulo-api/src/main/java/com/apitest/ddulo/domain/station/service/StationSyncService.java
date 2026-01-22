package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.metadata.domain.ApiMetadata;
import com.apitest.ddulo.domain.metadata.repository.ApiMetadataRepository;
import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.dto.PuzzleStationResponseDto;
import com.apitest.ddulo.domain.station.dto.StationItemDto;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationSyncService {

    private static final String API_NAME = "PUZZLE_STATION_META";

    private final StationApiClient stationApiClient;
    private final StationRepository stationRepository;
    private final ApiMetadataRepository apiMetadataRepository;

    @Transactional
    public void syncStationsIfNeeded() {

        ApiMetadata metadata = apiMetadataRepository
                .findByApiName(API_NAME)
                .orElseGet(() ->
                        apiMetadataRepository.save(
                                ApiMetadata.builder()
                                        .apiName(API_NAME)
                                        .updateIntervalDay(null) // 최초 1회
                                        .build()
                        )
                );

        // 🔐 중복 실행 방지
        if (!metadata.isUpdatable()) {
            return;
        }

        int offset = 0;
        int limit = 100;

        while (true) {
            PuzzleStationResponseDto response =
                    stationApiClient.fetchStations(offset, limit);

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
            if (offset >= response.getStatus().getTotalCount()) {
                break;
            }
        }

        metadata.markUpdated();
    }
}
