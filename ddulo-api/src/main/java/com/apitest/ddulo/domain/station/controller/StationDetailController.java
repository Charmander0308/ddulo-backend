package com.apitest.ddulo.domain.station.controller;

import com.apitest.ddulo.domain.station.dto.StationArrivalResponse;
import com.apitest.ddulo.domain.station.dto.StationDetailResponse;
import com.apitest.ddulo.domain.station.service.StationRealtimeService;
import com.apitest.ddulo.domain.station.service.StationService;
import com.apitest.ddulo.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/station-detail")
@RequiredArgsConstructor
@Tag(name = "지하철역 상세정보 API", description = "역 상세정보를 조회할 수 있는 REST API")
public class StationDetailController {

    private final StationRealtimeService stationRealtimeService;
    private final StationService stationService;

    //테스트용
    @GetMapping("/{station_id}")
    @Operation(summary = "역 상세정보 조회", description = "역 ID를 통해 해당 역의 상세정보를 조회할 수 있다.")
    public ApiResponse<StationArrivalResponse> stationDetailInfo(
            @PathVariable("station_id") @Parameter(description = "역 ID", example = "221") Long stationId) {
        return ApiResponse.success(stationRealtimeService.getRealtimeStationDetail(stationId));
    }

    //실제 사용되는 컨트롤러는 이것
//    @GetMapping("/{station_id}")
//    @Operation(summary = "역 상세정보 조회", description = "역 ID를 통해 해당 역의 상세정보를 조회할 수 있다.")
//    public ApiResponse<StationDetailResponse> stationDetailInfo(
//            @PathVariable("station_id") @Parameter(description = "역 ID", example = "221") Long stationId) {
//        return ApiResponse.success(stationService.getStationDetail(stationId));
//    }

}
