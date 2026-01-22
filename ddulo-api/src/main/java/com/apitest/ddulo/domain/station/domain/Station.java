package com.apitest.ddulo.domain.station.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station")
@Getter // 엔티티 접근용
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 필수
@AllArgsConstructor // Builder 내부용
@Builder // builder() 생성
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId;

    @Column(nullable = false, unique = true)
    private String stationCode;

    @Column(nullable = false)
    private String stationName;

    @Column(nullable = false)
    private String lineName; // "1호선"

    // created_at, updated_at
}

