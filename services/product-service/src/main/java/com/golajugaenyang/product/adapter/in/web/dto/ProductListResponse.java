package com.golajugaenyang.product.adapter.in.web.dto;

import com.golajugaenyang.product.application.product.port.in.dto.ProductListResult;
import java.util.List;

public record ProductListResponse(
    List<ProductCardResponse> items,
    String nextCursor,
    boolean hasNext
) {

    public static ProductListResponse from(ProductListResult result) {
        List<ProductCardResponse> items = result.items().stream()
            .map(ProductCardResponse::from)
            .toList();
        return new ProductListResponse(items, result.nextCursor(), result.hasNext());
    }

}
