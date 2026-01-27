package com.apitest.ddulo.domain.Route.Controller;

import com.apitest.ddulo.domain.Route.Service.ResultService;
import com.apitest.ddulo.domain.Route.dto.ResultRequest;
import com.apitest.ddulo.domain.Route.dto.ResultResponse;
import com.apitest.ddulo.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/result")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ApiResponse<ResultResponse> result(@RequestBody ResultRequest request){
        return ApiResponse.success(resultService.getResult(request));
    }

}
