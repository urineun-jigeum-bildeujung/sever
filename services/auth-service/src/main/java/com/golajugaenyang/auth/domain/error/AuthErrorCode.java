package com.golajugaenyang.auth.domain.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_LOGIN_CODE(
        HttpStatus.BAD_REQUEST,
        "AUTH_400_INVALID_LOGIN_CODE",
        "유효하지 않거나 만료된 로그인 코드입니다."
    ),
    INVALID_TOKEN(
            HttpStatus.BAD_REQUEST,
            "AUTH_400_INVALID_TOKEN",
            "유효하지 않은 인증입니다."
    ),
    MEMBER_ID_MISMATCH(
        HttpStatus.FORBIDDEN,
        "AUTH_403_MEMBER_ID_MISMATCH",
        "정보가 일치하지 않습니다."
    ),
    TOO_MANY_REQUESTS(
        HttpStatus.TOO_MANY_REQUESTS,
        "AUTH_429_TOO_MANY_REQUESTS",
        "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
    ),
    INVALID_AUTH(
        HttpStatus.NOT_FOUND,
        "AUTH_404_INVALID_AUTH",
        "존재하지 않는 인증 정보입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
