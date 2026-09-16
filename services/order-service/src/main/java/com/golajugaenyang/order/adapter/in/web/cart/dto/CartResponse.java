package com.golajugaenyang.order.adapter.in.web.cart.dto;

import com.golajugaenyang.order.application.cart.port.in.dto.CartResult;
import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long memberId,
    List<CartItemResponse> items,
    BigDecimal totalAmount
) {

    public static CartResponse from(CartResult result) {
        List<CartItemResponse> items = result.items().stream()
            .map(CartItemResponse::from)
            .toList();
        return new CartResponse(result.memberId(), items, result.totalAmount());
    }
}
