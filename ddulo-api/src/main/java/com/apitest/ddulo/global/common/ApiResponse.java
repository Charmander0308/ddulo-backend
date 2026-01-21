package com.apitest.ddulo.global.common;

import com.apitest.ddulo.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String result; // SUCCESS, FAIL
    private final String message;
    private final T data;
    private final String errorCode;

    // 성공 시
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data, null);
    }
    
    // 성공이지만 데이터는 없을 때 (예: 삭제 성공)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("SUCCESS", null, null, null);
    }

    // 실패 시 (ErrorCode 사용)
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>("FAIL", errorCode.getMessage(), null, errorCode.name());
    }

    // 실패 시 (메시지 직접 입력)
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>("FAIL", message, null, errorCode.name());
    }

    private ApiResponse(String result, String message, T data, String errorCode) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }
}
