package com.golajugaenyang.order.application.cart.port.out.dto;

import java.math.BigDecimal;

public record ProductSummary(
    Long productId,
    String productName,
    String thumbnailUrl,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    boolean purchasable,
    String availability
) {

}
