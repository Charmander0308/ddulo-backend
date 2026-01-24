package com.apitest.ddulo.domain.station.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteResponse {
    private int totalTime;          // 총 소요 시간 (초)
    private int transferCount;      // 환승 횟수
    private List<RouteLeg> legs;    // 구간별 상세 정보

    @Getter
    @Builder
    public static class RouteLeg {
        private String startStation; // 구간 출발역
        private String endStation;   // 구간 도착역
        private String lineName;     // 이용 노선
        private int sectionTime;     // 구간 소요 시간
    }
}