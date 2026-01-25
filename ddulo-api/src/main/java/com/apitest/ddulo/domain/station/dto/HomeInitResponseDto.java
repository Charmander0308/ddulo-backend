package com.apitest.ddulo.domain.station.dto;

import com.apitest.ddulo.domain.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeInitResponseDto {
    // 내 주변 1km 이내 역 (추천용, 상단 노출)
    private List<StationDto> nearbyStations;

    // 전체 역 리스트 (검색용, 로컬 DB 캐싱용)
    private List<StationDto> allStations;

    @Getter
    @Builder
    public static class StationDto {
        private Long stationId;
        private String stationName;
        private String lineName;
        private Double latitude;
        private Double longitude;

        // 거리 정보는 nearby 리스트에만 값이 있고, all에는 null일 수 있음
        private Integer distanceMeters;

        public static StationDto fromEntity(Station station, Integer distance) {
            return StationDto.builder()
                    .stationId(station.getStationId())
                    .stationName(station.getStationName())
                    .lineName(station.getLineName())
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .distanceMeters(distance)
                    .build();
        }
    }
}
