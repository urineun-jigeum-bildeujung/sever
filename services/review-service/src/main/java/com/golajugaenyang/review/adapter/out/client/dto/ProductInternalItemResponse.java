package com.golajugaenyang.review.adapter.out.client.dto;

public record ProductInternalItemResponse(
        Long productId,
        String thumbnailUrl,
        String productName,
        String categoryCode
) {
}
