package com.golajugaenyang.order.application.cart.port.in.dto;

import com.golajugaenyang.order.domain.cart.CartItemType;

public record ChangeCartItemQuantityCommand(
    Long memberId,
    CartItemType itemType,
    Long itemId,
    int delta
) {

}
