package com.golajugaenyang.order.adapter.out.external.product.dto;

import java.math.BigDecimal;


public record ProductInternalItemResponse(
    Long productId,
    Long productGroupId,
    String thumbnailUrl,
    String productName,
    String categoryCode,
    boolean replenishable,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    BigDecimal netQuantityValue,
    String netQuantityUnit,
    String quantityDimension,
    BigDecimal normalizedQuantityValue,
    String normalizedQuantityUnit,
    boolean purchasable,
    String availability
) {

}
