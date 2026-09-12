package com.golajugaenyang.product.adapter.in.internal.product.dto;

import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalLookupResult;
import java.util.List;

public record ProductInternalItemsResponse(
    List<ProductInternalItemResponse> items,
    List<Long> missingProductIds
) {

    public static ProductInternalItemsResponse from(
        ProductInternalLookupResult result
    ) {
        List<ProductInternalItemResponse> items = result.items().stream()
            .map(ProductInternalItemResponse::from)
            .toList();
        return new ProductInternalItemsResponse(items, result.missingProductIds());
    }
}
