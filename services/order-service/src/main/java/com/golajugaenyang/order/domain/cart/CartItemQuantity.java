package com.golajugaenyang.order.domain.cart;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CartItemQuantity {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 99;

    public static int clamp(int quantity) {
        return Math.clamp(quantity, MIN_QUANTITY, MAX_QUANTITY);
    }
}
