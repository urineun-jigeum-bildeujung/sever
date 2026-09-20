package com.golajugaenyang.review.adapter.out.client.dto;

import java.util.List;

public record ProductInternalItemsResponse(
        List<ProductInternalItemResponse> items,
        List<Long> missingProductIds
) {
}
