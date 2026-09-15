package com.golajugaenyang.order.domain.cart;

public record CartItem(
    CartItemKey key,
    int quantity
) {

    public CartItem {
        quantity = CartItemQuantity.clamp(quantity);
    }
}
