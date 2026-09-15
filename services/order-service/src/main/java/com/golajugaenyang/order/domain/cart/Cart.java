package com.golajugaenyang.order.domain.cart;

import java.util.List;

public record Cart(
    Long memberId,
    List<CartItem> items
) {

    public static Cart empty(Long memberId) {
        return new Cart(memberId, List.of());
    }
}
