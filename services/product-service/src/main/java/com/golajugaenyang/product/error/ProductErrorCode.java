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
        "유효하지 않은 커서 값입니다."),
    PRODUCT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "PRODUCT_404_PRODUCT_NOT_FOUND",
        "상품이 존재하지 않습니다."),
    INSUFFICIENT_STOCK(
        HttpStatus.CONFLICT,
        "PRODUCT_409_INSUFFICIENT_STOCK",
        "재고가 부족한 상품이 있습니다."),
    STOCK_MOVEMENT_CONFLICT(
        HttpStatus.CONFLICT,
        "PRODUCT_409_STOCK_MOVEMENT_CONFLICT",
        "재고 반영 조건이 맞지 않습니다."),
    STOCK_MOVEMENT_PRECONDITION_NOT_MET(
        HttpStatus.CONFLICT,
        "PRODUCT_409_STOCK_MOVEMENT_PRECONDITION_NOT_MET",
        "선행 재고 처리 기록이 존재하지 않습니다."),
    INVALID_TIME_DEAL_STATUS(
        HttpStatus.BAD_REQUEST,
        "PRODUCT_400_INVALID_TIME_DEAL_STATUS",
        "지원하지 않는 타임딜 상태입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
