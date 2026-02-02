package com.apitest.ddulo.domain.station.service;

import com.apitest.ddulo.domain.station.dto.request.PythonStationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PythonApiClient {

    private final RestTemplate restTemplate;

    @Value("${python.api.url.station}")
    private String pythonStationUrl;

    public void requestStationCalculation(PythonStationRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PythonStationRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(pythonStationUrl, entity, String.class);
            log.info("Python API call success for station: {}", request.getStationId());
        } catch (Exception e) {
            log.error("Python API call failed: {}", e.getMessage());
        }
    }
}
