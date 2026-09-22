package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MyReviewListResponse(
        List<Item> content,
        boolean hasNext
) {
    public record Item(
            Long reviewId,
            Long productId,
            String productName,
            String productImage,
            double rating,
            String text,
            LocalDate createdAt
    ) {
    }
}
