package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.QuantityUnit;
import java.math.BigDecimal;

public record ProductListProjection(
    Long id,
    String thumbnailUrl,
    String productName,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal normalizedQuantityValue,
    QuantityUnit normalizedQuantityUnit,
    BigDecimal avgRating,
    Integer reviewCount,
    Integer salesCount
) {

}
