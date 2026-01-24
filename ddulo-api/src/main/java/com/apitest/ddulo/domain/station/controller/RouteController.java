package com.apitest.ddulo.domain.station.controller;

import com.apitest.ddulo.domain.station.dto.RouteResponse;
import com.apitest.ddulo.domain.station.service.RouteService;
import com.apitest.ddulo.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ApiResponse<RouteResponse> searchRoute(
            @RequestParam("from") String fromStation,
            @RequestParam("to") String toStation
    ) {
        RouteResponse response = routeService.getSubwayRoute(fromStation, toStation);
        return ApiResponse.success(response);
    }

}