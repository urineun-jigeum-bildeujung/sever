package com.golajugaenyang.review.adapter.in.web.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record WritableProductListResponse(
        List<Item> content
) {

    public record Item(
            Long orderProductId,
            Long productId,
            String productName,
            String thumbnailUrl,
            OffsetDateTime confirmedAt
    ) {

    }
}
