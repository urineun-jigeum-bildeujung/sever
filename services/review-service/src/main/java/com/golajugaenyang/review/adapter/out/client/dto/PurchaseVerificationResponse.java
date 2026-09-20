package com.golajugaenyang.review.adapter.out.client.dto;

import java.util.List;

public record PurchaseVerificationResponse(
        List<PurchaseVerificationItem> items
) {
    public record PurchaseVerificationItem(
            String orderStatus,
            String itemStatus
    ) {
    }
}
