package com.golajugaenyang.order.domain.cart;

import java.util.Objects;

public record CartItemKey(
    CartItemType itemType,
    Long itemId
) {

    private static final String DELIMITER = ":";

    public CartItemKey {
        Objects.requireNonNull(itemType, "itemType은 null일 수 없습니다.");
        Objects.requireNonNull(itemId, "itemId는 null일 수 없습니다.");
    }

    public String toRedisField() {
        return itemType.name() + DELIMITER + itemId;
    }

    public static CartItemKey fromRedisField(String field) {
        String[] parts = field.split(DELIMITER, 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cart redis field: " + field);
        }
        return new CartItemKey(CartItemType.valueOf(parts[0]), Long.valueOf(parts[1]));
    }
}
