package com.golajugaenyang.order.adapter.out.persistence.cart;

import java.time.Instant;

public record CartRedisValue(
    int quantity,
    Instant addedAt
) {

    private static final char DELIMITER = ':';

    static CartRedisValue parse(String raw) {
        int idx = raw.indexOf(DELIMITER);
        if (idx < 0) {
            return new CartRedisValue(Integer.parseInt(raw), Instant.EPOCH);
        }
        int quantity = Integer.parseInt(raw.substring(0, idx));
        long epochMillis = Long.parseLong(raw.substring(idx + 1));
        return new CartRedisValue(quantity, Instant.ofEpochMilli(epochMillis));
    }
}
