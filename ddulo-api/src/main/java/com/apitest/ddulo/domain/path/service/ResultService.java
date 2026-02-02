package com.apitest.ddulo.domain.path.service;

import com.apitest.ddulo.domain.path.dto.external.redis.PathFullData;
import com.apitest.ddulo.domain.path.dto.response.PathPredictionResponse;
import com.apitest.ddulo.domain.path.dto.response.ResultResponse;
import com.apitest.ddulo.domain.station.dto.response.FastestPathResponse;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import com.apitest.ddulo.global.exception.CustomException;
import com.apitest.ddulo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {

    private final PathRedisService pathRedisService;
    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public ResultResponse getResult(
            FastestPathResponse fastestPathResponse,
            PathPredictionResponse pathPredictionResponse){
        return buildResultResponse(fastestPathResponse, pathPredictionResponse);
    }
    
    // 최적경로정보로 최종결과반환DTO를 만드는 메서드
    private ResultResponse buildResultResponse(
            FastestPathResponse fastestPathResponse,
            PathPredictionResponse pathPredictionResponse) {
        // (탑승)구간별 상세정보 목록
        List<FastestPathResponse.RouteLeg> legs = fastestPathResponse.getLegs();

        // 시작역과 종료역 코드 조회
        String startStationCode = getStationCode(
                legs.get(0).getStartStation(),
                legs.get(0).getLineName()
        );
        String endStationCode = getStationCode(
                legs.get(legs.size() - 1).getEndStation(),
                legs.get(legs.size() - 1).getLineName()
        );

        // Redis 데이터 조회
        PathFullData pathFullData = pathRedisService.getFullPath(startStationCode, endStationCode);
        if (pathFullData == null) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        return ResultResponse.builder()
                .totalTimeSecond(fastestPathResponse.getTotalTime())
                .estimatedBoardingTime(LocalDateTime.parse(pathFullData.getEstimatedBoardingTime()))
                .boardingProbability(pathFullData.getBoardingProbability())
                .startStation(mapStationInfoList(pathFullData.getStartStation()))
                .transferStation(mapTransferStations(pathFullData.getTransferStation()))
                .endStation(mapStationInfo(pathFullData.getEndStation()))
                .build();
    }

    // 역 코드 조회 헬퍼 메서드
    private String getStationCode(String stationName, String lineName) {
        return String.valueOf(
                stationRepository.findStationCodeByNameAndLine(stationName, lineName)
                        .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND))
        );
    }

    private List<ResultResponse.StationInfo> mapStationInfoList(List<PathFullData.StationInfo> sourceList) {
        if (sourceList == null) return null;
        return sourceList.stream()
                .map(this::mapStationInfo)
                .toList();
    }

    private ResultResponse.StationInfo mapStationInfo(PathFullData.StationInfo source) {
        if (source == null) return null;
        return ResultResponse.StationInfo.builder()
                .stationCode(source.getStationId())
                .stationName(source.getStationName())
                .lineName(source.getLineName())
                .estimatedWaitingSec(source.getEstimatedWaitingSec())
                .isBoardable(source.isBoardable())
                .results(mapResultInfoList(source.getResults()))
                .build();
    }

    private List<ResultResponse.TransferStationGroup> mapTransferStations(List<PathFullData.TransferSection> sourceList) {
        if (sourceList == null) return null;
        return IntStream.range(0, sourceList.size())
                .mapToObj(i -> ResultResponse.TransferStationGroup.builder()
                        .transferOrder(i + 1)
                        .options(mapStationInfoList(sourceList.get(i).getStations()))
                        .build())
                .toList();
    }

    private List<ResultResponse.ResultInfo> mapResultInfoList(List<PathFullData.DetailResult> sourceList) {
        if (sourceList == null) return null;
        return sourceList.stream()
                .map(this::mapResultInfo)
                .toList();
    }

    private ResultResponse.ResultInfo mapResultInfo(PathFullData.DetailResult source) {
        if (source == null) return null;
        return ResultResponse.ResultInfo.builder()
                .carCongestions(mapCarCongestions(source.getCarCongestions()))
                .stationCongestions(mapStationCongestions(source.getStationCongestions()))
                .totalCongestions(mapStationCongestions(source.getTotalCongestions()))
                .bestBoardings(mapBoardingInfos(source.getBestBoardings()))
                .comfortBoarding(mapBoardingInfos(source.getComfortBoarding()))
                .build();
    }

    private List<ResultResponse.CarCongestion> mapCarCongestions(List<PathFullData.CarCongestion> sourceList) {
        if (sourceList == null) return null;
        return sourceList.stream()
                .map(s -> ResultResponse.CarCongestion.builder()
                        .carNo(s.getCarNo())
                        .congestionLevel(s.getCongestionLevel())
                        .build())
                .toList();
    }

    private List<ResultResponse.StationCongestion> mapStationCongestions(List<PathFullData.DoorCongestion> sourceList) {
        if (sourceList == null) return null;
        return sourceList.stream()
                .map(s -> ResultResponse.StationCongestion.builder()
                        .carNo(s.getCarNo())
                        .doorNo(s.getDoorNo())
                        .congestionLevel(s.getCongestionLevel())
                        .build())
                .toList();
    }

    private List<ResultResponse.BoardingInfo> mapBoardingInfos(List<PathFullData.BoardingSpot> sourceList) {
        if (sourceList == null) return null;
        return sourceList.stream()
                .map(s -> ResultResponse.BoardingInfo.builder()
                        .carNo(s.getCarNo())
                        .doorNo(s.getDoorNo())
                        .build())
                .toList();
    }
}
