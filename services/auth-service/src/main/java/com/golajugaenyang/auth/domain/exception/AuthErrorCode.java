package com.golajugaenyang.auth.domain.exception;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_LOGIN_CODE(
        HttpStatus.BAD_REQUEST,
        "AUTH_400",
        "유효하지 않거나 만료된 로그인 코드입니다."
    ),
    MEMBER_ID_MISMATCH(
        HttpStatus.FORBIDDEN,
        "AUTH_403",
        "정보가 일치하지 않습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
