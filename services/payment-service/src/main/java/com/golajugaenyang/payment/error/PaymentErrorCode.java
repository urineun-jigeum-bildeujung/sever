package com.golajugaenyang.payment.error;


import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "PAYMENT_404_NOT_FOUND",
        "결제 정보를 찾을 수 없습니다."
    ),
    ORDER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "PAYMENT_404_ORDER_NOT_FOUND",
        "주문 정보를 찾을 수 없습니다."
    ),
    ORDER_OWNER_MISMATCH(
        HttpStatus.FORBIDDEN,
        "PAYMENT_403_ORDER_OWNER_MISMATCH",
        "본인의 주문에 대해서만 결제할 수 있습니다."
    ),
    ORDER_NOT_PAYABLE(
        HttpStatus.CONFLICT,
        "PAYMENT_409_ORDER_NOT_PAYABLE",
        "결제 가능한 상태의 주문이 아닙니다."
    ),
    PAYMENT_NOT_CONFIRMABLE(
        HttpStatus.CONFLICT,
        "PAYMENT_409_NOT_CONFIRMABLE",
        "이미 처리되었거나 승인할 수 없는 결제입니다."
    ),
    AMOUNT_MISMATCH(
        HttpStatus.CONFLICT,
        "PAYMENT_409_AMOUNT_MISMATCH",
        "결제 금액이 일치하지 않습니다."
    ),
    TOSS_CONFIRM_FAILED(
        HttpStatus.BAD_GATEWAY,
        "PAYMENT_502_TOSS_CONFIRM_FAILED",
        "결제 승인에 실패했습니다."
    ),
    TOSS_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "PAYMENT_503_TOSS_SERVICE_UNAVAILABLE",
        "결제 서비스에 일시적으로 연결할 수 없습니다."
    ),
    TOSS_CANCEL_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "PAYMENT_500_TOSS_CANCEL_FAILED",
        "자동 취소 처리에 실패했습니다. 수동 확인이 필요합니다."
    ),
    ORDER_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "PAYMENT_503_ORDER_SERVICE_UNAVAILABLE",
        "주문 서비스를 일시적으로 확인할 수 없습니다."
    ),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
