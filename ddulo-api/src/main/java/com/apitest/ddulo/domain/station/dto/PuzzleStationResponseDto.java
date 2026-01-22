package com.apitest.ddulo.domain.station.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PuzzleStationResponseDto {

    private Status status;
    private List<StationItemDto> contents;

    @Getter
    public static class Status {
        private String code;
        private int totalCount;
        private int offset;
        private int limit;
    }
}

