package com.apitest.ddulo.domain.subway.controller;

import com.apitest.ddulo.domain.subway.dto.StationDetailResponseDto;
import com.apitest.ddulo.domain.subway.service.StationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StationDetailController {

    private final StationDetailService stationDetailService;

    @GetMapping("/station-detail/{stationId}")
    public StationDetailResponseDto getStationDetail(
            @PathVariable Long stationId
    ) {
        return stationDetailService.getStationDetail(stationId);
    }
}

