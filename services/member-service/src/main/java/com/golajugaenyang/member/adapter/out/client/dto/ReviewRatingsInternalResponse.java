package com.golajugaenyang.member.adapter.out.client.dto;

import java.util.List;

public record ReviewRatingsInternalResponse(
        List<Item> items
) {

    public record Item(
            Long productId,
            double averageRating,
            long reviewCount
    ) {
    }
}
