package com.golajugaenyang.product.application.timedeal.port.in.dto;

import java.math.BigDecimal;

public record TimeDealListItem(
    Long productId,
    Long timeDealItemId,
    String thumbnailUrl,
    String productName,
    BigDecimal normalPrice,
    BigDecimal discountedPrice,
    BigDecimal discountRate,
    BigDecimal dailyPrice,
    StockBadge stockBadge
) {

}
