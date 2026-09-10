package com.golajugaenyang.product.error;


import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_CURSOR(
        HttpStatus.BAD_REQUEST,
        "PRODUCT_400_INVALID_CURSOR",
        "유효하지 않은 커서 값입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
