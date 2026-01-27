package com.apitest.ddulo.domain.Route.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ResultRequest {
    private String result;
    private String message;
    private RouteData data; // data 객체
    private String errorCode;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class RouteData {
        private int totalTime;
        private int transferCount;
        private List<Leg> legs; // legs 배열
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Leg {
        private String startStation;
        private String endStation;
        private String lineName;
        private int sectionTime;
    }
}
