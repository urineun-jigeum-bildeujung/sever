package com.golajugaenyang.product.adapter.in.internal.timedeal.dto;

import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalItem;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TimeDealItemInternalItemResponse(
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

    public static TimeDealItemInternalItemResponse from(TimeDealItemInternalItem item) {
        return new TimeDealItemInternalItemResponse(
            item.timeDealItemId(), item.dealId(),
            item.productId(), item.productGroupId(),
            item.thumbnailUrl(), item.productName(),
            item.categoryCode() != null ? item.categoryCode().name() : null,
            item.replenishable(),
            item.normalPrice(), item.discountedPrice(), item.discountRate(),
            item.netQuantityValue(),
            item.netQuantityUnit() != null ? item.netQuantityUnit().getSymbol() : null,
            item.quantityDimension() != null ? item.quantityDimension().name() : null,
            item.normalizedQuantityValue(),
            item.normalizedQuantityUnit() != null
                ? item.normalizedQuantityUnit().getSymbol() : null,
            item.remainingQuantity(), item.perUserQuantityLimit(),
            item.purchasable(), item.availability().name(),
            item.dealEndAt()
        );
    }
}
