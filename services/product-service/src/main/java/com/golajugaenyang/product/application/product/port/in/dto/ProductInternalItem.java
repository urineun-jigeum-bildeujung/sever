package com.golajugaenyang.product.application.product.port.in.dto;

import com.golajugaenyang.common.core.domain.CategoryCode;
import com.golajugaenyang.common.core.domain.QuantityDimension;
import com.golajugaenyang.common.core.domain.QuantityUnit;
import com.golajugaenyang.common.core.pricing.PriceCalculator;
import com.golajugaenyang.product.application.product.port.out.dto.ProductInternalProjection;
import java.math.BigDecimal;

public record ProductInternalItem(
    Long productId,
    Long productGroupId,
    String thumbnailUrl,
    String productName,
    CategoryCode categoryCode,
    boolean replenishable,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    BigDecimal netQuantityValue,
    QuantityUnit netQuantityUnit,
    QuantityDimension quantityDimension,
    BigDecimal normalizedQuantityValue,
    QuantityUnit normalizedQuantityUnit,
    ProductAvailability availability
) {

    public boolean purchasable() {
        return availability == ProductAvailability.AVAILABLE;
    }

    public static ProductInternalItem from(ProductInternalProjection p) {

        return new ProductInternalItem(
            p.id(), p.productGroupId(), p.thumbnailUrl(), p.productName(),
            p.categoryCode(), p.replenishable(),
            p.price(), p.originalPrice(),
            PriceCalculator.discountRate(p.originalPrice(), p.price()),
            p.netQuantityValue(), p.netQuantityUnit(),
            p.quantityDimension(),
            p.normalizedQuantityValue(), p.normalizedQuantityUnit(),
            ProductAvailability.from(p.status())
        );
    }
}
