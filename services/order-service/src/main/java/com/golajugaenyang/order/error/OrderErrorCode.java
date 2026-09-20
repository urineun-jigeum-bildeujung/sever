package com.golajugaenyang.order.error;

import com.golajugaenyang.common.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    CART_ITEM_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "ORDER_404_CART_ITEM_NOT_FOUND",
        "장바구니에 해당 상품이 존재하지 않습니다."),
    ORDER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "ORDER_404_ORDER_NOT_FOUND",
        "요청한 주문을 찾을 수 없습니다."),
    PRODUCT_NOT_PURCHASABLE(
        HttpStatus.CONFLICT,
        "ORDER_409_PRODUCT_NOT_PURCHASABLE",
        "현재 구매할 수 없는 상품입니다."),
    PRODUCT_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "ORDER_503_PRODUCT_SERVICE_UNAVAILABLE",
        "상품 정보를 일시적으로 확인할 수 없습니다. 잠시 후 다시 시도해주세요."),
    PRODUCT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "ORDER_404_PRODUCT_NOT_FOUND",
        "요청한 상품 정보를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(
        HttpStatus.CONFLICT,
        "ORDER_409_INSUFFICIENT_STOCK",
        "재고가 부족한 상품이 있습니다."),
    STOCK_MOVEMENT_CONFLICT(
        HttpStatus.CONFLICT,
        "ORDER_409_STOCK_MOVEMENT_CONFLICT",
        "재고 처리 중 충돌이 발생했습니다."),
    INVENTORY_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "ORDER_503_INVENTORY_SERVICE_UNAVAILABLE",
        "재고 서비스를 일시적으로 확인할 수 없습니다."),
    INVENTORY_REQUEST_INVALID(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "ORDER_500_INVENTORY_REQUEST_INVALID",
        "재고 예약 요청 처리 중 오류가 발생했습니다."),
    ORDER_OWNER_MISMATCH(
        HttpStatus.FORBIDDEN,
        "ORDER_403_OWNER_MISMATCH",
        "본인의 주문이 아닙니다."),
    ORDER_NOT_CANCELLABLE(
        HttpStatus.CONFLICT,
        "ORDER_409_NOT_CANCELLABLE",
        "현재 상태의 주문은 취소할 수 없습니다."
    ),
    ORDER_NOT_CONFIRMABLE(
        HttpStatus.CONFLICT,
        "ORDER_409_NOT_CONFIRMABLE",
        "구매 확정 가능한 상태가 아닙니다."),
    ORDER_NOT_CLAIMABLE(
        HttpStatus.CONFLICT,
        "ORDER_409_NOT_CLAIMABLE",
        "반품/교환 신청 가능한 기간이 아닙니다."),
    ORDER_ITEM_NOT_FOUND(
        HttpStatus.NOT_FOUND
        , "ORDER_404_ITEM_NOT_FOUND",
        "요청한 주문 품목을 찾을 수 없습니다."),
    CLAIM_ITEM_QUANTITY_EXCEEDED(
        HttpStatus.CONFLICT,
        "ORDER_409_CLAIM_ITEM_QUANTITY_EXCEEDED",
        "신청 가능한 수량을 초과했습니다."
    ),
    CLAIM_ALREADY_IN_PROGRESS(
        HttpStatus.CONFLICT,
        "ORDER_409_CLAIM_ALREADY_IN_PROGRESS",
        "이미 처리 중인 반품/교환 신청이 있습니다."
    ),
    INVALID_CLAIM_TYPE(
        HttpStatus.BAD_REQUEST,
        "ORDER_400_INVALID_CLAIM_TYPE",
        "유효하지 않은 신청 유형입니다."),
    INVALID_CURSOR(
        HttpStatus.BAD_REQUEST,
        "ORDER_400_INVALID_CURSOR",
        "유효하지 않은 커서 값입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
