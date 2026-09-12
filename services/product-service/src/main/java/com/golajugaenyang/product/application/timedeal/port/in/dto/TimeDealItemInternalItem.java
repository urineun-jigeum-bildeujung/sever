package com.golajugaenyang.product.application.timedeal.port.in.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.application.product.port.in.dto.ProductAvailability;
import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealItemInternalProjection;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record TimeDealItemInternalItem(
    Long timeDealItemId,
    Long dealId,
    Long productId,
    Long productGroupId,
    String thumbnailUrl,
    String productName,
    CategoryCode categoryCode,
    boolean replenishable,
    BigDecimal normalPrice,
    BigDecimal discountedPrice,
    BigDecimal discountRate,
    BigDecimal netQuantityValue,
    QuantityUnit netQuantityUnit,
    QuantityDimension quantityDimension,
    BigDecimal normalizedQuantityValue,
    QuantityUnit normalizedQuantityUnit,
    int remainingQuantity,
    int perUserQuantityLimit,
    TimeDealItemAvailability availability,
    OffsetDateTime dealEndAt
) {

    public boolean purchasable() {
        return availability == TimeDealItemAvailability.AVAILABLE;
    }

    public static TimeDealItemInternalItem from(
        TimeDealItemInternalProjection p
    ) {
        int remaining = p.quantityLimit() - p.reservedQuantity() - p.soldQuantity();
        
        return new TimeDealItemInternalItem(
            p.timeDealItemId(), p.dealId(),
            p.productId(), p.productGroupId(),
            p.thumbnailUrl(), p.productName(), p.categoryCode(),
            p.replenishable(),
            p.normalPrice(), p.discountedPrice(), p.discountRate(),
            p.netQuantityValue(), p.netQuantityUnit(),
            p.quantityDimension(),
            p.normalizedQuantityValue(), p.normalizedQuantityUnit(),
            Math.max(remaining, 0), p.perUserQuantityLimit(),
            TimeDealItemAvailability.from(p.dealStatus(), p.itemStatus(), remaining),
            p.dealEndAt()
        );
    }
}
