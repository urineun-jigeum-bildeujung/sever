package com.golajugaenyang.order.application.cart.port.in.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResult(
    Long memberId,
    List<CartItemResult> items,
    BigDecimal totalAmount
) {

    public static CartResult empty(Long memberId) {
        return new CartResult(memberId, List.of(), BigDecimal.ZERO);
    }
}
