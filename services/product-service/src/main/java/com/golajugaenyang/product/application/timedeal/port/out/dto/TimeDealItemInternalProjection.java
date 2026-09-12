package com.golajugaenyang.product.application.timedeal.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TimeDealItemInternalProjection(
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
    int quantityLimit,
    int reservedQuantity,
    int soldQuantity,
    int perUserQuantityLimit,
    TimeDealItemStatus itemStatus,
    TimeDealStatus dealStatus,
    OffsetDateTime dealEndAt
) {

}
