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
        "장바구니에 해당 상품이 존재하지 않습니다."
    ),
    PRODUCT_NOT_PURCHASABLE(
        HttpStatus.CONFLICT,
        "ORDER_409_PRODUCT_NOT_PURCHASABLE",
        "현재 구매할 수 없는 상품입니다."
    ),
    PRODUCT_SERVICE_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "ORDER_503_PRODUCT_SERVICE_UNAVAILABLE",
        "상품 정보를 일시적으로 확인할 수 없습니다. 잠시 후 다시 시도해주세요."
    ),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
