package com.golajugaenyang.order.application.order.port.in.dto;

import java.time.OffsetDateTime;

public record PurchaseVerificationResult(
    Long orderId,
    Long orderItemId,
    String orderStatus,
    OffsetDateTime confirmedAt,
    String itemStatus,
    OffsetDateTime orderedAt
) {

}
