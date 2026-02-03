package com.apitest.ddulo.domain.station.facade;

import com.apitest.ddulo.domain.station.dto.internal.StationAdjacencyResult;
import com.apitest.ddulo.domain.station.dto.request.PythonStationRequest;
import com.apitest.ddulo.domain.station.dto.response.StationArrivalResponse;
import com.apitest.ddulo.domain.station.dto.external.redis.StationDetailData;
import com.apitest.ddulo.domain.station.client.PythonApiClient;
import com.apitest.ddulo.domain.station.service.StationAdjacencyService;
import com.apitest.ddulo.domain.station.service.StationRedisService;
import com.apitest.ddulo.global.exception.CustomException;
import com.apitest.ddulo.global.exception.ErrorCode;
import com.apitest.ddulo.global.utils.DateUtils;
import com.apitest.ddulo.global.utils.SubwayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationRealtimeFacade {

    private final StationRedisService stationRedisService;
    private final StationAdjacencyService stationAdjacencyService;
    private final PythonApiClient pythonApiClient;

    @Transactional(readOnly = true)
    public StationArrivalResponse getRealtimeStationDetail(String stationCode) {
        //없는 역코드면 예외처리
        stationAdjacencyService.validateStationExists(stationCode);

        // 1. Python API 호출 (Redis 적재 트리거)
        triggerPythonCalculation(stationCode);

        //redis에서 실시간 역/열차 정보 조회
        StationDetailData stationDetailData = stationRedisService.getStationDetail(stationCode);
        //데이터가 없는 경우 예외처리
        if(stationDetailData == null) throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        //인접 역 리스트 조회
        StationAdjacencyResult nearStations = stationAdjacencyService.getAdjacentStations(stationCode);
        //인접 역이 모두 없는 경우 예외처리
        if(nearStations.getPrevStations().isEmpty() && nearStations.getNextStations().isEmpty())
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);

        return buildStationArrivalResponse(stationDetailData, nearStations);
    }

    private void triggerPythonCalculation(String stationCode) {
        try {
            String dayKey = DateUtils.convertDateToDayKey(String.valueOf(LocalDate.now()));

            // 1. 현재 시간 기준 10분 단위 내림/올림 시간 구하기 (HHmm)
            String currentTimeKey = DateUtils.getCurrentTimeKey();
            String nextTimeKey = DateUtils.getNextTimeKey();

            // 2. HH:mm 형식으로 변환
            String current = formatTime(currentTimeKey);
            String next = formatTime(nextTimeKey);

            // 3. 리스트 생성 (상/하행 구분 없이 시간대만 전달)
            List<String> times = List.of(current, next);

            PythonStationRequest request = PythonStationRequest.builder()
                    .dayOfWeek(dayKey)
                    .stationId(stationCode)
                    .leftTimes(times)
                    .rightTimes(times)
                    .build();

            pythonApiClient.requestStationCalculation(request);

        } catch (Exception e) {
            log.error("Failed to trigger python calculation for station {}: {}", stationCode, e.getMessage());
        }
    }

    private String formatTime(String hhmm) {
        if (hhmm == null || hhmm.length() != 4) return hhmm;
        return hhmm.substring(0, 2) + ":" + hhmm.substring(2);
    }

    //데이터를 정제해서 프론트엔드로 넘기는 dto를 생성
    private StationArrivalResponse buildStationArrivalResponse(
            StationDetailData stationDetailData,
            StationAdjacencyResult nearStations) {

        var station = stationDetailData.getStation();
        String lineName = station.getLineName();

        return StationArrivalResponse.builder()
                .station(StationArrivalResponse.StationInfo.builder()
                        .stationCode(station.getStationId())
                        .stationName(station.getStationName())
                        .lineName(lineName)
                        .prevStations(nearStations.getPrevStations())
                        .nextStations(nearStations.getNextStations())
                        .build())
                .upBound(stationDetailData.getUpBound().stream()
                        .map(arrival -> mapToArrivalInfo(arrival, lineName))
                        .toList())
                .downBound(stationDetailData.getDownBound().stream()
                        .map(arrival -> mapToArrivalInfo(arrival, lineName))
                        .toList())
                .build();
    }

    //열차 실시간 데이터 리스트 담는 메서드
    private StationArrivalResponse.ArrivalInfo mapToArrivalInfo(
            StationDetailData.TrainArrival arrival,
            String lineName) {

        return StationArrivalResponse.ArrivalInfo.builder()
                .direction(SubwayUtils.getDirectionName(arrival.getDirection(), lineName))
                .arrivalSec(arrival.getArrivalSec())
                .currentStation(arrival.getCurrentStation())
                .destination(arrival.getDestination())
                .isBoardable(arrival.isBoardable())

                .carCongestions(arrival.getCarCongestions().stream()
                        .map(congestion -> StationArrivalResponse.CarCongestion.builder()
                                .carNo(congestion.getCarNo())
                                .congestionLevel(congestion.getCongestionLevel())
                                .build())
                        .toList())
                .build();
    }

}
