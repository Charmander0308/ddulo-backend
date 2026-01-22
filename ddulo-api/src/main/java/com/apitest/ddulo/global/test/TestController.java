package com.apitest.ddulo.global.test;

import com.apitest.ddulo.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        log.info("빠른 API 테스트 시작");
        LocalDateTime l = LocalDateTime.now();
        return ApiResponse.success("pong: " + l);
    }

    @GetMapping("/slow")
    public ApiResponse<String> slow() throws InterruptedException {
        log.info("느린 API 테스트 시작...");
        Thread.sleep(1500);
        return ApiResponse.success("slow response");
    }

    @GetMapping("/error")
    public ApiResponse<Void> error() {
        throw new RuntimeException("테스트용 에러입니다!");
    }
}