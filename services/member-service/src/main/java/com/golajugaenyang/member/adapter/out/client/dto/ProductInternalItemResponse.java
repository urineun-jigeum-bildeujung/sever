package com.golajugaenyang.member.adapter.out.client.dto;

import java.math.BigDecimal;

public record ProductInternalItemResponse(
        Long productId,
        String thumbnailUrl,
        String productName,
        String categoryCode,
        BigDecimal price
) {
}
