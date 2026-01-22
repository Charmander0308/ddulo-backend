package com.apitest.ddulo.domain.station.repository;

import com.apitest.ddulo.domain.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository
        extends JpaRepository<Station, Long> {

    boolean existsByStationCode(String stationCode);

    Optional<Station> findByStationCode(String stationCode);

    List<Station> findByStationNameContaining(String keyword);
}
