package com.golajugaenyang.product.application.product.port.out.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.product.domain.product.ProductStatus;
import java.math.BigDecimal;

public record ProductInternalProjection(
    Long id,
    Long productGroupId,
    String thumbnailUrl,
    String productName,
    CategoryCode categoryCode,
    boolean replenishable,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal netQuantityValue,
    QuantityUnit netQuantityUnit,
    QuantityDimension quantityDimension,
    BigDecimal normalizedQuantityValue,
    QuantityUnit normalizedQuantityUnit,
    ProductStatus status
) {

}
