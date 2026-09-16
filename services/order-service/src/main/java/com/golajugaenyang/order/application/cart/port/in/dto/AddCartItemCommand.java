package com.golajugaenyang.order.application.cart.port.in.dto;

import com.golajugaenyang.order.domain.cart.CartItemType;


public record AddCartItemCommand(
    Long memberId,
    CartItemType itemType,
    Long itemId,
    int quantity
) {

}
