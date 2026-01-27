package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.station.dto.StationArrivalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationRealtimeService {

    @Transactional
    public StationArrivalResponse getRealtimeStationDetail(Long stationId) {
        return sample(stationId);
    }

    private StationArrivalResponse sample(Long stationId){
        List<StationArrivalResponse.ArrivalInfo> upBoundList = List.of(
                StationArrivalResponse.ArrivalInfo.builder()
                        .direction("성수(내선)행")
                        .arrivalMsg("2분 55초 후")
                        .trainNo("2234")
                        .currentStation("역삼역")
                        .destination("성수")
                        .carCongestions(List.of(
                                StationArrivalResponse.CarCongestion.builder().carNo(1).congestionLevel(0).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(2).congestionLevel(10).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(3).congestionLevel(20).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(4).congestionLevel(30).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(5).congestionLevel(40).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(6).congestionLevel(60).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(7).congestionLevel(70).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(8).congestionLevel(80).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(9).congestionLevel(90).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(10).congestionLevel(100).build()
                        ))
                        .build()
        );
        List<StationArrivalResponse.ArrivalInfo> downBoundList = List.of(
                StationArrivalResponse.ArrivalInfo.builder()
                        .direction("신도림(외선)행")
                        .arrivalMsg("2분 30초 후")
                        .trainNo("2235")
                        .currentStation("교대역")
                        .destination("신도림")
                        .carCongestions(List.of(
                                StationArrivalResponse.CarCongestion.builder().carNo(1).congestionLevel(100).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(2).congestionLevel(90).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(3).congestionLevel(80).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(4).congestionLevel(70).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(5).congestionLevel(60).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(6).congestionLevel(40).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(7).congestionLevel(30).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(8).congestionLevel(20).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(9).congestionLevel(10).build(),
                                StationArrivalResponse.CarCongestion.builder().carNo(10).congestionLevel(0).build()
                        ))
                        .build()
        );

        return StationArrivalResponse.builder()
                .station(StationArrivalResponse.StationInfo.builder()
                        .stationId(stationId)
                        .stationName("강남역")
                        .lineName("2호선")
                        .prevStationName("역삼역")
                        .nextStationName("교대역")
                        .build())
                .upBound(upBoundList)
                .downBound(downBoundList)
                .build();
    }

}
