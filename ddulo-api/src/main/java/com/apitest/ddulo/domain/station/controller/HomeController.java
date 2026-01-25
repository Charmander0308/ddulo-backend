package com.apitest.ddulo.domain.station.controller;

import com.apitest.ddulo.domain.station.dto.HomeInitResponseDto;
import com.apitest.ddulo.domain.station.service.HomeService;
import com.apitest.ddulo.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/load")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ApiResponse<HomeInitResponseDto> initializeApp(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        return ApiResponse.success(homeService.getHomeData(lat, lon));
    }
}
