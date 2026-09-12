package com.golajugaenyang.product.application.product.port.in.dto;

import com.golajugaenyang.product.domain.product.ProductStatus;
import lombok.Getter;

@Getter
public enum ProductAvailability {
    AVAILABLE,
    OUT_OF_STOCK,
    DISCONTINUED;

    public static ProductAvailability from(ProductStatus status) {
        return switch (status) {
            case ON_SALE -> AVAILABLE;
            case SOLD_OUT -> OUT_OF_STOCK;
            case DISCONTINUED -> ProductAvailability.DISCONTINUED;
        };
    }
}
