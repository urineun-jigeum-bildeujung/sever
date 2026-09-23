package com.golajugaenyang.review.adapter.out.client.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ConfirmedItemsResponse(
        List<ConfirmedItem> items
) {

    public record ConfirmedItem(
            Long productId,
            Long petId,
            Long orderId,
            Long orderItemId,
            String orderStatus,
            String itemStatus,
            OffsetDateTime confirmedAt
    ) {

    }
}
