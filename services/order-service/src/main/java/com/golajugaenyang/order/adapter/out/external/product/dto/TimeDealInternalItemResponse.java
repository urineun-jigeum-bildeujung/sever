package com.golajugaenyang.order.adapter.out.external.product.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record TimeDealInternalItemResponse(
    Long timeDealItemId,
    Long dealId,
    Long productId,
    Long productGroupId,
    String thumbnailUrl,
    String productName,
    String categoryCode,
    boolean replenishable,
    BigDecimal normalPrice,
    BigDecimal discountedPrice,
    BigDecimal discountRate,
    BigDecimal netQuantityValue,
    String netQuantityUnit,
    String quantityDimension,
    BigDecimal normalizedQuantityValue,
    String normalizedQuantityUnit,
    int remainingQuantity,
    int perUserQuantityLimit,
    boolean purchasable,
    String availability,
    OffsetDateTime dealEndAt
) {

}
