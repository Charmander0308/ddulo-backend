package com.apitest.ddulo.domain.station.controller;

import com.apitest.ddulo.domain.station.dto.RouteResponse;
import com.apitest.ddulo.domain.station.service.RouteService;
import com.apitest.ddulo.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
@Tag(name = "최단시간 경로 API", description = "출발지에서 도착지까지의 최단시간 경로를 조회할 수 있는 REST API")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @Operation(summary = "최단시간 경로 조회", description = "출발지에서 도착지까지의 최단시간 경로를 조회할 수 있다.")
    public ApiResponse<RouteResponse> searchRoute(
            @RequestParam("from") @Parameter(description = "출발역", example = "역삼") String fromStation,
            @RequestParam("to") @Parameter(description = "도착역", example = "광명사거리") String toStation
    ) {
        RouteResponse response = routeService.getSubwayRoute(fromStation, toStation);
        return ApiResponse.success(response);
    }

}