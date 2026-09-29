package com.golajugaenyang.product.application.timedeal.port.out.dto;

import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TimeDealPricingProjection(
    Long timeDealItemId,
    Long dealId,
    Long productId,
    BigDecimal normalPrice,
    BigDecimal discountedPrice,
    BigDecimal discountRate,
    int quantityLimit,
    int reservedQuantity,
    int soldQuantity,
    TimeDealItemStatus itemStatus,
    TimeDealStatus dealStatus,
    OffsetDateTime dealStartAt,
    OffsetDateTime dealEndAt
) {

}
