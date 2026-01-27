package com.apitest.ddulo.domain.Route.Controller;

import com.apitest.ddulo.domain.Route.Service.ResultService;
import com.apitest.ddulo.domain.Route.dto.ResultRequest;
import com.apitest.ddulo.domain.Route.dto.ResultResponse;
import com.apitest.ddulo.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/result")
@RequiredArgsConstructor
@Tag(name = "최종결과 API", description = "선택한 경로에 대한 결과를 반환하는 REST API")
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    @Operation(summary = "선택한 경로에 대한 결과를 반환", description = "경로를 통해 결과화면에 필요한 데이터를 모두 받을 수 있다.")
    public ApiResponse<ResultResponse> result(@RequestBody ResultRequest request){
        return ApiResponse.success(resultService.getResult(request));
    }

}
