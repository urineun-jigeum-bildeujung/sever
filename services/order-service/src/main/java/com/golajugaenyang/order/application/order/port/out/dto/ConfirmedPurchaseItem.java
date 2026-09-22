package com.golajugaenyang.order.application.order.port.out.dto;

import java.time.OffsetDateTime;

public record ConfirmedPurchaseItem(
    Long productId,
    Long petId,
    Long orderId,
    Long orderItemId,
    String orderStatus,
    String itemStatus,
    OffsetDateTime confirmedAt
) {

}
