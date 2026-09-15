package com.golajugaenyang.order.application.cart.port.out.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TimeDealSummary(
    Long timeDealItemId,
    Long productId,
    String productName,
    String thumbnailUrl,
    BigDecimal discountedPrice,
    BigDecimal normalPrice,
    BigDecimal discountRate,
    int remainingQuantity,
    int perUserQuantityLimit,
    boolean purchasable,
    String availability,
    OffsetDateTime dealEndAt
) {

}
