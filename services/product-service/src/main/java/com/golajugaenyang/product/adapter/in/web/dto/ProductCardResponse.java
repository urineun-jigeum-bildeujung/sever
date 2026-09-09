package com.golajugaenyang.product.adapter.in.web.dto;

import com.golajugaenyang.product.application.product.port.in.dto.ProductListItem;
import java.math.BigDecimal;

public record ProductCardResponse(
    Long productId,
    String thumbnailUrl,
    String productName,
    BigDecimal discountRate,
    BigDecimal price,
    BigDecimal unitPrice,
    String unitLabel,
    BigDecimal avgRating,
    Integer reviewCount
) {

    public static ProductCardResponse from(ProductListItem item) {
        return new ProductCardResponse(
            item.id(), item.thumbnailUrl(), item.productName(),
            item.discountRate(), item.price(), item.unitPrice(), item.unitLabel(),
            item.avgRating(), item.reviewCount());
    }
}
