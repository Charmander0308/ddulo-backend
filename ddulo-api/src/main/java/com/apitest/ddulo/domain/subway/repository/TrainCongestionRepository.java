package com.apitest.ddulo.domain.subway.repository;

import com.apitest.ddulo.domain.subway.domain.TrainCongestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainCongestionRepository
        extends JpaRepository<TrainCongestion, Long> {
}
