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

}
