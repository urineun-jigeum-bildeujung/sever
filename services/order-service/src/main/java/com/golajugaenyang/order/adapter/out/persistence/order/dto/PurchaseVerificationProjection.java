package com.golajugaenyang.order.adapter.out.persistence.order.dto;

import com.golajugaenyang.order.domain.order.OrderItemStatus;
import com.golajugaenyang.order.domain.order.OrderStatus;
import java.time.OffsetDateTime;

public record PurchaseVerificationProjection(
    Long orderId,
    Long orderItemId,
    OrderStatus orderStatus,
    OffsetDateTime confirmedAt,
    OrderItemStatus itemStatus,
    OffsetDateTime orderedAt
) {

}
