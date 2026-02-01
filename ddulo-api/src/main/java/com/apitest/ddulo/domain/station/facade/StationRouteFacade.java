package com.apitest.ddulo.domain.station.facade;

import com.apitest.ddulo.domain.path.service.PathPredictionService;
import com.apitest.ddulo.domain.path.service.ResultService;
import com.apitest.ddulo.domain.station.service.FastPathDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationRouteFacade {

    private final FastPathDataService fastPathDataService;  // 최단경로 조회 서비스
    private final PathPredictionService pathPredictionService;  // 경로 예측 요약 정보 조회 서비스
    private final ResultService resultService;  // 최종결과 반환 서비스



}
