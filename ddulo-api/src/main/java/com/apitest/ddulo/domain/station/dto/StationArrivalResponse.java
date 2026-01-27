package com.apitest.ddulo.domain.station.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StationArrivalResponse {
    private StationInfo station;    // 노선 기본정보

    private List<ArrivalInfo> upBound;   // 상행/내선 (최대 3개)
    private List<ArrivalInfo> downBound; // 하행/외선 (최대 3개)

    @Getter
    @Builder
    public static class StationInfo {
        private Long stationId; // 역 ID(pk)
        private String stationName;     // 역명
        private String lineName;    // 노선명
        private String prevStationName;     //이전 역 명
        private String nextStationName;     //다음 역 명
    }

    @Getter
    @Builder
    public static class ArrivalInfo {
        private String direction;    // 방면 (예: 성수행)
        private String arrivalMsg;   // 도착 메시지 (예: 2분 30초 후, 전역 도착)
        private String trainNo;      // 열차 번호
        private String currentStation; // 현재 열차 위치
        private String destination;  // 종착역

        private List<CarCongestion> carCongestions; //열차 칸별 혼잡도
    }

    @Getter
    @Builder
    public static class CarCongestion {
        private int carNo;      // 객차 번호(1~10)
        private int congestionLevel;    // 혼잡도 수치
    }
}
