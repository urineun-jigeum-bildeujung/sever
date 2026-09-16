package com.golajugaenyang.order.application.order.port.out.dto;

import java.math.BigDecimal;

public record CatalogItem(
    Long referenceId,
    boolean isTimeDeal,
    Long productId,
    Long dealItemId,
    Long productGroupId,
    String productName,
    String thumbnailUrl,
    String categoryCode,
    boolean replenishable,
    BigDecimal unitPrice,
    BigDecimal unitDiscountAmount,
    BigDecimal netQuantityValue,
    String netQuantityUnit,
    String quantityDimension,
    BigDecimal normalizedQuantityValue,
    String normalizedQuantityUnit,
    boolean purchasable,
    String availability
) {

}
