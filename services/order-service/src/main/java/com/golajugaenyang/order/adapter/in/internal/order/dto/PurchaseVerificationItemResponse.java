package com.golajugaenyang.order.adapter.in.internal.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.PurchaseVerificationResult;
import java.time.OffsetDateTime;

public record PurchaseVerificationItemResponse(
    Long orderId,
    Long orderItemId,
    String orderStatus,
    OffsetDateTime confirmedAt,
    String itemStatus,
    OffsetDateTime orderedAt
) {

    public static PurchaseVerificationItemResponse from(PurchaseVerificationResult r) {
        return new PurchaseVerificationItemResponse(
            r.orderId(),
            r.orderItemId(),
            r.orderStatus(),
            r.confirmedAt(),
            r.itemStatus(),
            r.orderedAt()
        );
    }
}
