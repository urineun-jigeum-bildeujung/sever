package com.golajugaenyang.product.application.timedeal.port.out.dto;

import com.golajugaenyang.product.domain.timedeal.TimeDealItemStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TimeDealListRowProjection(
    Long dealId,
    String dealName,
    OffsetDateTime dealStartAt,
    OffsetDateTime dealEndAt,
    Long timeDealItemId,
    Long productId,
    String thumbnailUrl,
    String productName,
    BigDecimal normalPrice,
    BigDecimal discountedPrice,
    BigDecimal discountRate,
    int quantityLimit,
    int reservedQuantity,
    int soldQuantity,
    TimeDealItemStatus itemStatus
) {

}
