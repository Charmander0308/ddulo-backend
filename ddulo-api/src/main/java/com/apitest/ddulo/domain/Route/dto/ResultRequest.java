package com.apitest.ddulo.domain.Route.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "경로 검색 결과를 통해 받은 데이터 DTO")
public class ResultRequest {
    @Schema(description = "경로 검색 결과 정보")
    private RouteData data; // data 객체

    @Getter
    @NoArgsConstructor
    @Schema(description = "경로 검색 결과 정보")
    public static class RouteData {
        @Schema(description = "총 소요시간", example = "1080")
        private int totalTime;
        @Schema(description = "총 환승횟수", example = "0")
        private int transferCount;
        @Schema(description = "구간별 상세 정보")
        private List<Leg> legs; // legs 배열
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "구간별 상세 정보")
    public static class Leg {
        @Schema(description = "출발역", example = "강남")
        private String startStation;
        @Schema(description = "도착역", example = "대림")
        private String endStation;
        @Schema(description = "노선명", example = "2호선")
        private String lineName;
        @Schema(description = "소요시간", example = "1080")
        private int sectionTime;
    }
}
