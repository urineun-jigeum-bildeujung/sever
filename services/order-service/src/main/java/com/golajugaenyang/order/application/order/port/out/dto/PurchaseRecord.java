package com.golajugaenyang.order.application.order.port.out.dto;

import java.time.OffsetDateTime;

public record PurchaseRecord(
    Long orderId,
    Long orderItemId,
    String orderStatus,
    OffsetDateTime confirmedAt,
    String itemStatus,
    OffsetDateTime orderedAt
) {

}
