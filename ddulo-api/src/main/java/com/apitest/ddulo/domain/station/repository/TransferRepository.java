package com.apitest.ddulo.domain.station.repository;

import com.apitest.ddulo.domain.station.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
