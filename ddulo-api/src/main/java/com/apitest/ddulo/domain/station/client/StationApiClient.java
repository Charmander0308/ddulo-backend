package com.apitest.ddulo.domain.station.client;

import com.apitest.ddulo.domain.station.dto.PuzzleStationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class StationApiClient {

    @Value("${sk.api.key}")
    private String apiKey;

    @Value("${sk.api.url.station}")
    private String stationUrl;

    private final RestTemplate restTemplate;

    public PuzzleStationResponseDto fetchStations(int offset, int limit) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(stationUrl)
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        HttpHeaders headers = new HttpHeaders();
        headers.set("appkey", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PuzzleStationResponseDto> response =
                restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        PuzzleStationResponseDto.class
                );

        return response.getBody();
    }
}


