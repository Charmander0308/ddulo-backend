package com.apitest.ddulo.domain.station.dto.response;

import com.apitest.ddulo.domain.path.dto.response.ResultResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationTotalResponse {
    private FastestPathResponse fastestPathResponse;

    private ResultResponse resultResponse;
}
