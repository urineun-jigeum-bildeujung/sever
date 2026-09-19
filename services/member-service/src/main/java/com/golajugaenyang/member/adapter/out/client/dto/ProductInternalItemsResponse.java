package com.golajugaenyang.member.adapter.out.client.dto;

import java.util.List;

public record ProductInternalItemsResponse(
        List<ProductInternalItemResponse> items,
        List<Long> missingProductIds
) {
}
