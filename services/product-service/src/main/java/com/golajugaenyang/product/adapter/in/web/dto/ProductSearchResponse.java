package com.golajugaenyang.product.adapter.in.web.dto;

import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchResult;
import java.util.List;

public record ProductSearchResponse(
    List<ProductCardResponse> items,
    String nextCursor,
    boolean hasNext,
    Long totalCount
) {

    public static ProductSearchResponse from(ProductSearchResult result) {
        List<ProductCardResponse> items = result.items().stream()
            .map(ProductCardResponse::from)
            .toList();
        return new ProductSearchResponse(
            items,
            result.nextCursor(),
            result.hasNext(),
            result.totalCount()
        );
    }
}
