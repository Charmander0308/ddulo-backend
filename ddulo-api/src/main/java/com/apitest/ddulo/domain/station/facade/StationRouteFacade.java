package com.apitest.ddulo.domain.station.facade;

import com.apitest.ddulo.domain.route.service.ResultService;
import com.apitest.ddulo.domain.station.service.FastPathDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationRouteFacade {

    private final FastPathDataService fastPathDataService;
    //이 자리에 "최적경로 조회시 뜨는 예상 출발/도착시간, 대기시간, 탑승확률"을 DTO로 정제해주는 서비스도 포함시켜야함
    private final ResultService resultService;




}
