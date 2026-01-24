package com.apitest.ddulo.domain.station.initializer;

import com.apitest.ddulo.domain.station.service.StationSyncService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StationDataInitializer {

    private final StationSyncService stationSyncService;

    @PostConstruct
    public void init() {
        stationSyncService.syncStationsIfNeeded();
    }
}
