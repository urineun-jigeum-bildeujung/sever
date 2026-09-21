package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.Instant;
import java.util.List;

public record FeedbackCheckPendingListResponse(
        List<Item> content
) {

    public record Item(
            Long orderProductId,
            Long productId,
            String productName,
            String thumbnailUrl,
            Instant checkAvailableAt,
            Long petId
    ) {

    }
}
