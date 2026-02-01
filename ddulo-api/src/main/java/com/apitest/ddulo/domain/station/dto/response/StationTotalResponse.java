package com.apitest.ddulo.domain.station.dto.response;

import com.apitest.ddulo.domain.route.dto.ResultResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationTotalResponse {
    private FastestPathResponse fastestPathResponse;

    private ResultResponse resultResponse;
}
