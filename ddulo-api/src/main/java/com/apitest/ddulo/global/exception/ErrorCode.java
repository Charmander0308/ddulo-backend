package com.apitest.ddulo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	//1001, 1002, 1003, ..., 2001, 2002, ... 식으로 천 단위로 커스텀 에러코드를 지정
	//에러코드를 아래로 계속 추가

	// [1000 ~] : 공통 에러
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, 1000, "올바르지 않은 요청입니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 1001, "입력값이 올바르지 않습니다."),
	EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1002, "외부 API 연동 중 오류가 발생했습니다."),
	
	// [9000 ~] : 시스템/서버 에러
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "서버 에러가 발생하였습니다. 관리자에게 문의해주세요.");
	
	private final HttpStatus status;
	private final int code;
	private final String message;
	
	private ErrorCode(HttpStatus status, int code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

}
