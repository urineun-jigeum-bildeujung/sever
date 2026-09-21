package com.golajugaenyang.order.application.order.port.in.dto;

import java.time.OffsetDateTime;

public record ConfirmedPurchaseItemResult(
    Long productId,
    Long orderId,
    Long orderItemId,
    String orderStatus,
    String itemStatus,
    OffsetDateTime confirmedAt
) {

}
