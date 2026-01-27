package com.apitest.ddulo.domain.Route.Service;

import com.apitest.ddulo.domain.Route.dto.ResultRequest;
import com.apitest.ddulo.domain.Route.dto.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {

    @Transactional
    public ResultResponse getResult(ResultRequest request){
        return sample(request);
    }

    private ResultResponse sample(ResultRequest request){
        return ResultResponse.builder()
                .totalTimeSecond(request.getData().getTotalTime())
                .estimatedBoardingTime(LocalDateTime.now())
                .boardingProbability(96.5)

                .startStation(createStartStation())
                .transferStation(createAllTransferStations())
                .endStation(createEndStation())
                .build();
    }

    private ResultResponse.StationInfo createStartStation() {
        return ResultResponse.StationInfo.builder()
                .stationId(221L)
                .stationName("역삼역")
                .lineName("2호선")
                .nextStationName("강남역")
                .estimatedWatingSec(150)
                .results(List.of(
                        ResultResponse.ResultInfo.builder()
                                .carCongestions(createAllCarCongestions())
                                .stationCongestions(createAllDoorCongestions())
                                .bestBoardings(createAllBestBoardings())
                                .comfortBoarding(createAllComfortBoardings())
                                .arrivalInfos(List.of(
                                        ResultResponse.ArrivalInfo.builder()
                                                .direction("내선")
                                                .arrivalSec(175)
                                                .currentStation("선릉역")
                                                .destination("성수(내선)역")
                                                .build(),
                                        ResultResponse.ArrivalInfo.builder()
                                                .direction("내선")
                                                .arrivalSec(355)
                                                .currentStation("삼성역")
                                                .destination("성수(내선)역")
                                                .build(),
                                        ResultResponse.ArrivalInfo.builder()
                                                .direction("내선")
                                                .arrivalSec(505)
                                                .currentStation("종합운동장역")
                                                .destination("성수(내선)역")
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private ResultResponse.StationInfo createEndStation(){
        return ResultResponse.StationInfo.builder()
                .stationId(342L)
                .stationName("광명사거리역")
                .lineName("7호선")
                .nextStationName(null)
                .estimatedWatingSec(0)
                .results(null)
                .build();
    }

    private List<ResultResponse.StationInfo> createAllTransferStations() {
        List<ResultResponse.StationInfo> list = new ArrayList<>();
        list.add(ResultResponse.StationInfo.builder()
                        .stationId(338L)
                        .stationName("대림역")
                        .lineName("7호선")
                        .nextStationName("남구로역")
                        .estimatedWatingSec(150)
                        .results(List.of(
                                ResultResponse.ResultInfo.builder()
                                        .carCongestions(createAllCarCongestions())
                                        .stationCongestions(createAllDoorCongestions())
                                        .bestBoardings(createAllBestBoardings())
                                        .comfortBoarding(createAllComfortBoardings())
                                        .arrivalInfos(List.of(
                                                ResultResponse.ArrivalInfo.builder()
                                                        .direction("하행")
                                                        .arrivalSec(175)
                                                        .currentStation("신풍역")
                                                        .destination("온수역")
                                                        .build(),
                                                ResultResponse.ArrivalInfo.builder()
                                                        .direction("하행")
                                                        .arrivalSec(355)
                                                        .currentStation("보라매역")
                                                        .destination("석남역")
                                                        .build(),
                                                ResultResponse.ArrivalInfo.builder()
                                                        .direction("하행")
                                                        .arrivalSec(505)
                                                        .currentStation("신대방삼거리역")
                                                        .destination("온수역")
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
        );

        return list;
    }

    private List<ResultResponse.CarCongestion> createAllCarCongestions() {
        List<ResultResponse.CarCongestion> list = new ArrayList<>();

        for(int car = 1; car <= 10; car++){
            list.add(ResultResponse.CarCongestion.builder()
                    .carNo(car)
                    .congestionLevel(car * 10)
                    .build());
        }

        return list;
    }

    // 샘플 데이터 자동 생성
    private List<ResultResponse.StationCongestion> createAllDoorCongestions() {
        List<ResultResponse.StationCongestion> list = new ArrayList<>();

        for (int car = 1; car <= 10; car++) {
            for (int door = 1; door <= 4; door++) {
                list.add(ResultResponse.StationCongestion.builder()
                        .carNo(car)
                        .doorNo(door)
                        .congestionLevel((car * 10) + door)
                        .build());
            }
        }
        return list;
    }

    private List<ResultResponse.BoardingInfo> createAllBestBoardings() {
        List<ResultResponse.BoardingInfo> list = new ArrayList<>();
        list.add(ResultResponse.BoardingInfo.builder()
                .carNo(7)
                .doorNo(1)
                .build());
        list.add(ResultResponse.BoardingInfo.builder()
                .carNo(4)
                .doorNo(1)
                .build());
        return list;
    }

    private List<ResultResponse.BoardingInfo> createAllComfortBoardings() {
        List<ResultResponse.BoardingInfo> list = new ArrayList<>();
        list.add(ResultResponse.BoardingInfo.builder()
                .carNo(5)
                .doorNo(3)
                .build());
        list.add(ResultResponse.BoardingInfo.builder()
                .carNo(5)
                .doorNo(4)
                .build());
        return list;
    }


}
