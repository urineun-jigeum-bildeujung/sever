package com.golajugaenyang.member.adapter.in.web.dto.response;

import java.math.BigDecimal;

public record WishlistItemResponse(
        Long productId,
        String thumbnailUrl,
        boolean wished,
        String productName,
        BigDecimal price,
        BigDecimal originalPrice,
        BigDecimal reviewScore,
        int reviewCount
) {
}
