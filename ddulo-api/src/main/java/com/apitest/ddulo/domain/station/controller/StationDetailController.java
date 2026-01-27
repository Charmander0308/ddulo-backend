package com.apitest.ddulo.domain.station.controller;

import com.apitest.ddulo.domain.station.dto.StationArrivalResponse;
import com.apitest.ddulo.domain.station.service.StationRealtimeService;
import com.apitest.ddulo.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/station-detail")
@RequiredArgsConstructor
public class StationDetailController {

    private final StationRealtimeService stationRealtimeService;

    @GetMapping("/{station_id}")
    public ApiResponse<StationArrivalResponse> stationDetailInfo(
            @PathVariable("station_id") Long stationId) {
        return ApiResponse.success(stationRealtimeService.getRealtimeStationDetail(stationId));
    }

}
