package com.apitest.ddulo.domain.Route.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ResultResponse {
    private Integer totalTimeSecond;    //예상 소요시간(초)
    private LocalDateTime estimatedBoardingTime;   //예상 탑승시각
    private Double boardingProbability;     //예상 탑승 가능 확률

    private StationInfo startStation;   //출발역
    private List<StationInfo> transferStation;  //환승역
    private StationInfo endStation;     //도착역

    @Getter
    @Builder
    public static class StationInfo {
        private Long stationId; // 역 ID(pk)
        private String stationName;     // 역명
        private String lineName;    // 노선명
        private String nextStationName;     //다음 역 명

        private List<ResultInfo> results;    //결과
    }

    @Getter
    @Builder
    public static class ResultInfo {
        private List<CarCongestion> carCongestions; //열차 칸별 혼잡도
        private List<StationCongestion> stationCongestions; //플랫폼 게이트 별(ex: 2-1) 혼잡도
        private List<BoardingInfo> bestBoardings;   //베스트 탑승
        private List<BoardingInfo> comfortBoarding; //쾌적한 탑승
        private List<ArrivalInfo> arrivalInfos;     //가장 가까운 3개 열차 데이터
    }

    @Getter
    @Builder
    public static class CarCongestion {
        private Integer carNo;      // 객차 번호(1~10)
        private Integer congestionLevel;    // 혼잡도 수치
    }

    @Getter
    @Builder
    public static class StationCongestion {
        private Integer carNo;      // 객차 번호(1~10)
        private Integer doorNo;     // 문 번호
        private Integer congestionLevel;    // 혼잡도 수치
    }

    @Getter
    @Builder
    public static class BoardingInfo {
        private Integer carNo;  //객차번호
        private Integer doorNo;   //문 번호
    }

    @Getter
    @Builder
    public static class ArrivalInfo {
        private String direction;    // 방면 (예: 성수행)
        private String arrivalMsg;   // 도착 메시지 (예: 2분 30초 후, 전역 도착)
        private String trainNo;      // 열차 번호
        private String currentStation; // 현재 열차 위치
        private String destination;  // 종착역
    }
}
