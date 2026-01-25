package com.apitest.ddulo.domain.station.initializer;

import com.apitest.ddulo.domain.station.service.StationCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@DependsOn("stationDataInitializer")
public class StationCsvDataInitializer implements CommandLineRunner {
    private final StationCsvService stationCsvService;

    @Override
    public void run(String... args) throws Exception {
        stationCsvService.updateStationCoordinates("data/station_coords.csv");
        stationCsvService.updateStationCoordinates("data/station_coords2.csv");
    }
}
