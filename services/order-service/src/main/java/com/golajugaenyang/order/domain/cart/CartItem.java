package com.golajugaenyang.order.domain.cart;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public record CartItem(
    CartItemKey key,
    int quantity,
    Instant addedAt
) {

    public static final Comparator<CartItem> ADDED_ORDER =
        Comparator.comparing(CartItem::addedAt)
            .thenComparing(item -> item.key().toRedisField());

    public CartItem {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(addedAt, "addedAt must not be null");
        quantity = CartItemQuantity.clamp(quantity);
    }
}
