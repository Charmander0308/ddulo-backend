package com.apitest.ddulo.domain.path.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

//최종 화면에 대한 DTO
@Getter
@Builder
public class ResultResponse {
    private Integer totalTimeSecond;    //예상 소요시간(초)
    private LocalDateTime estimatedBoardingTime;   //예상 탑승시각
    private Double boardingProbability;     //예상 탑승 가능 확률

    private List<StationInfo> startStation;   //출발역
    private List<TransferStationGroup> transferStation;  //환승역
    private StationInfo endStation;     //도착역

    @Getter
    @Builder
    public static class StationInfo {
        private String stationCode; // 역 코드
        private String stationName;     // 역명
        private String lineName;    // 노선명
        private String nextStationName;     //다음 역 명
        private Integer estimatedWaitingSec; //예상 대기시간(초)
        private Boolean isBoardable;    //탑승 가능 여부

        private List<ResultInfo> results;    //결과
    }

    @Getter
    @Builder
    public static class TransferStationGroup {
        private int transferOrder;      // 환승 순서 (1, 2...)
        private List<StationInfo> options; // 해당 환승역 정보
    }

    @Getter
    @Builder
    public static class ResultInfo {
        private List<CarCongestion> carCongestions; //열차 칸별 혼잡도
        private List<StationCongestion> stationCongestions; //플랫폼 게이트 별(ex: 2-1) 혼잡도
        private List<StationCongestion> totalCongestions;   //역 + 열차 종합 혼잡도(Advanced라 사용 안해도 무방)
        private List<BoardingInfo> bestBoardings;   //베스트 탑승
        private List<BoardingInfo> comfortBoarding; //쾌적한 탑승
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
}
