package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.station.domain.Station;
import com.apitest.ddulo.domain.station.dto.HomeInitResponseDto;
import com.apitest.ddulo.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final StationRepository stationRepository;

    // 앱 초기 실행 시 호출되는 메서드
    // 전체 역 데이터 + 내 주변(1km) 역 데이터를 한 번에 반환
    @Transactional(readOnly = true)
    public HomeInitResponseDto getHomeData(Double userLat, Double userLon) {

        // DB에서 모든 역 데이터 가져오기 (쿼리 1회 발생), 캐싱
        List<Station> allStations = getAllStationsCached();

        // 전체 역 리스트 변환 (거리 정보 없음)
        List<HomeInitResponseDto.StationDto> allStationDtos = allStations.stream()
                .map(station -> HomeInitResponseDto.StationDto.fromEntity(station, null))
                .collect(Collectors.toList());

        // 내 주변 1km 이내 역 필터링 및 거리 계산
        List<HomeInitResponseDto.StationDto> nearbyStationDtos = allStations.stream()
                // 위경도 데이터가 없는 역은 계산에서 제외 (NPE 방지)
                .filter(station -> station.getLatitude() != null && station.getLongitude() != null)
                .map(station -> {
                    // 거리 계산 (미터 단위)
                    int distance = calculateDistance(userLat, userLon, station.getLatitude(), station.getLongitude());
                    return new StationWithDistance(station, distance); // 임시 객체로 매핑
                })
                .filter(dto -> dto.distance <= 1000) // 1km(1000m) 이내 필터링
                .sorted(Comparator.comparingInt(dto -> dto.distance)) // 가까운 순 정렬
//                .limit(5) // 너무 많으면 상위 N개만 출력
                .map(dto -> HomeInitResponseDto.StationDto.fromEntity(dto.station, dto.distance))
                .collect(Collectors.toList());

        // 결과 조립 및 반환
        return HomeInitResponseDto.builder()
                .nearbyStations(nearbyStationDtos)
                .allStations(allStationDtos)
                .build();
    }

    // 하버사인(Haversine) 공식을 이용한 두 좌표 사이의 거리 계산
    // @return 거리 (미터 단위)
    private int calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = R * c;

        // 미터(m) 단위로 반환 (소수점 버림)
        return (int) (distanceKm * 1000);
    }

    // 스트림 내부에서 거리와 역 정보를 묶어두기 위한 내부 클래스
    @RequiredArgsConstructor
    private static class StationWithDistance {
        final Station station;
        final int distance;
    }

    // 호출한 데이터를 메모리에 캐싱
    @Cacheable(value = "stations")
    public List<Station> getAllStationsCached() {
        return stationRepository.findAll();
    }
}