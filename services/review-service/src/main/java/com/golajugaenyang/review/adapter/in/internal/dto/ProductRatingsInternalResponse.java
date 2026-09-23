package com.golajugaenyang.review.adapter.in.internal.dto;

import com.golajugaenyang.review.domain.repository.ProductRatingSummary;
import java.util.List;

public record ProductRatingsInternalResponse(
        List<Item> items
) {

    public record Item(
            Long productId,
            double averageRating,
            long reviewCount
    ) {

        public static Item from(ProductRatingSummary summary) {
            return new Item(summary.productId(), summary.averageRating(), summary.reviewCount());
        }
    }
}
