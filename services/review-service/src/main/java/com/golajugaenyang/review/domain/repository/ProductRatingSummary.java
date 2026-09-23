package com.golajugaenyang.review.domain.repository;

public record ProductRatingSummary(
        Long productId,
        double averageRating,
        long reviewCount
) {
}
