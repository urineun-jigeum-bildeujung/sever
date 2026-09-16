package com.golajugaenyang.order.application.cart.port.in.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CartItemResult(
    String itemType,
    Long itemId,
    int quantity,
    boolean available,
    String unavailableReason,
    String productName,
    String thumbnailUrl,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    BigDecimal subtotal,
    OffsetDateTime dealEndAt
) {

    public static final String REASON_NOT_FOUND = "NOT_FOUND";
    public static final String REASON_DEAL_ENDED = "DEAL_ENDED";
    public static final String REASON_TEMPORARILY_UNAVAILABLE = "TEMPORARILY_UNAVAILABLE";
}
