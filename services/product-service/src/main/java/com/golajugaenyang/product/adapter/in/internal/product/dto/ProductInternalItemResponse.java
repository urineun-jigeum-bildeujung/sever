package com.golajugaenyang.product.adapter.in.internal.product.dto;

import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalItem;
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

    public static ProductInternalItemResponse from(ProductInternalItem item) {
        return new ProductInternalItemResponse(
            item.productId(), item.productGroupId(), item.thumbnailUrl(), item.productName(),
            item.categoryCode() != null ? item.categoryCode().name() : null,
            item.replenishable(),
            item.price(), item.originalPrice(), item.discountRate(),
            item.netQuantityValue(),
            item.netQuantityUnit() != null ? item.netQuantityUnit().getSymbol() : null,
            item.quantityDimension() != null ? item.quantityDimension().name() : null,
            item.normalizedQuantityValue(),
            item.normalizedQuantityUnit() != null
                ? item.normalizedQuantityUnit().getSymbol() : null,
            item.purchasable(),
            item.availability().name()
        );
    }
}
