package com.golajugaenyang.common.security.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonSecurityErrorCode implements ErrorCode {

    MISSING_AUTH_ID(
            HttpStatus.UNAUTHORIZED,
            "SECURITY_401_MISSING_AUTH_ID", "인증 정보가 없습니다."
    ),
    INVALID_INTERNAL_SECRET(
            HttpStatus.UNAUTHORIZED,
            "SECURITY_401_UNAUTHORIZED", "인증에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
