package com.golajugaenyang.common.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT_VALUE(
        HttpStatus.BAD_REQUEST,
        "COMMON_400",
        "요청 값이 유효하지 않습니다."
    ),
    INTERNAL_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "COMMON_500",
        "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
