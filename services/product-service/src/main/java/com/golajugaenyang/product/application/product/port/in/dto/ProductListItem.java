package com.golajugaenyang.product.application.product.port.in.dto;

import com.golajugaenyang.common.core.pricing.PriceCalculator;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import java.math.BigDecimal;

public record ProductListItem(
    Long id,
    String thumbnailUrl,
    String productName,
    BigDecimal price,
    BigDecimal discountRate,
    BigDecimal unitPrice,
    String unitLabel,
    BigDecimal avgRating,
    Integer reviewCount
) {

    public static ProductListItem from(ProductListProjection p) {
        return new ProductListItem(
            p.id(),
            p.thumbnailUrl(),
            p.productName(),
            p.price(),
            PriceCalculator.discountRate(p.originalPrice(), p.price()),
            PriceCalculator.unitPrice(p.price(), p.normalizedQuantityValue()),
            p.normalizedQuantityUnit().getSymbol(),
            p.avgRating(),
            p.reviewCount()
        );
    }

}
