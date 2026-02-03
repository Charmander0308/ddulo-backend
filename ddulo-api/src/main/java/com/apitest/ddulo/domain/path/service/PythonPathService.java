package com.apitest.ddulo.domain.path.service;

import com.apitest.ddulo.domain.path.dto.request.PythonPathRequest;
import com.apitest.ddulo.domain.station.domain.Exit;
import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.dto.response.FastestPathResponse;
import com.apitest.ddulo.domain.station.repository.ExitRepository;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import com.apitest.ddulo.domain.station.client.PythonApiClient;
import com.apitest.ddulo.global.exception.CustomException;
import com.apitest.ddulo.global.exception.ErrorCode;
import com.apitest.ddulo.global.utils.DateUtils;
import com.apitest.ddulo.global.utils.StationUtils;
import com.apitest.ddulo.global.utils.SubwayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PythonPathService {

    private final StationRepository stationRepository;
    private final ExitRepository exitRepository;
    private final PythonApiClient pythonApiClient;

    @Transactional(readOnly = true)
    public void triggerPythonPathCalculation(FastestPathResponse response) {
        try {
            List<FastestPathResponse.RouteLeg> legs = response.getLegs();
            if (legs.isEmpty()) return;

            // 1. 출발 정보
            FastestPathResponse.RouteLeg firstLeg = legs.get(0);
            Station startStation = stationRepository.findStationByStationNameAndLineName(
                    StationUtils.addStationSuffix(firstLeg.getStartStation()),
                    firstLeg.getLineName())
                    .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

            int startDirection = SubwayUtils.getDirectionNumberInt(firstLeg.getDirection());
            List<Integer> startFastBoarding = getFastBoarding(startStation, startDirection);
            
            String dayKey = DateUtils.convertDateToDayKey(String.valueOf(LocalDate.now()));
            List<String> startTimes = getCurrentTimeList(0);

            // 2. 환승 정보
            List<String> transferStationIds = new ArrayList<>();
            List<Integer> transferDirections = new ArrayList<>();
            List<List<String>> transferTimes = new ArrayList<>();
            List<List<Integer>> transferFastBoardings = new ArrayList<>();

            int accumulatedTime = firstLeg.getSectionTime();

            for (int i = 0; i < legs.size() - 1; i++) {
                FastestPathResponse.RouteLeg currentLeg = legs.get(i);
                FastestPathResponse.RouteLeg nextLeg = legs.get(i + 1);

                Station transferStation = stationRepository.findStationByStationNameAndLineName(
                        StationUtils.addStationSuffix(currentLeg.getEndStation()),
                        currentLeg.getLineName())
                        .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

                int transferDirection = SubwayUtils.getDirectionNumberInt(nextLeg.getDirection());

                transferStationIds.add(transferStation.getStationCode());
                transferDirections.add(transferDirection);
                transferTimes.add(getCurrentTimeList(accumulatedTime));
                transferFastBoardings.add(getFastBoarding(transferStation, transferDirection));

                accumulatedTime += nextLeg.getSectionTime();
            }

            // 3. 도착 정보
            FastestPathResponse.RouteLeg lastLeg = legs.get(legs.size() - 1);
            Station endStation = stationRepository.findStationByStationNameAndLineName(
                    StationUtils.addStationSuffix(lastLeg.getEndStation()),
                    lastLeg.getLineName())
                    .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

            PythonPathRequest request = PythonPathRequest.builder()
                    .dayOfWeek(dayKey)
                    .startStationId(startStation.getStationCode())
                    .startDirection(startDirection)
                    .startTime(startTimes)
                    .startFastBoarding(startFastBoarding)
                    .transferStationIds(transferStationIds)
                    .transferDirections(transferDirections)
                    .transferTimes(transferTimes)
                    .transferFastBoardings(transferFastBoardings)
                    .endStationId(endStation.getStationCode())
                    .build();

            pythonApiClient.requestPathCalculation(request);

        } catch (Exception e) {
            log.error("Failed to trigger python path calculation", e);
        }
    }

    private List<Integer> getFastBoarding(Station station, int direction) {
        List<Exit> exits = exitRepository.findByStationAndDirection(station, direction);
        if (exits.isEmpty()) return List.of(1, 1); // 기본값
        Exit exit = exits.get(0);
        return List.of(exit.getCarNumber(), exit.getDoorNumber());
    }

    private List<String> getCurrentTimeList(int addedSeconds) {
        LocalTime time = LocalTime.now().plusSeconds(addedSeconds);
        
        int minute = time.getMinute();
        int roundedMinute = (minute / 10) * 10;
        
        LocalTime current = time.withMinute(roundedMinute).withSecond(0);
        LocalTime next = current.plusMinutes(10);
        
        return List.of(
                current.format(DateTimeFormatter.ofPattern("HH:mm")),
                next.format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}
