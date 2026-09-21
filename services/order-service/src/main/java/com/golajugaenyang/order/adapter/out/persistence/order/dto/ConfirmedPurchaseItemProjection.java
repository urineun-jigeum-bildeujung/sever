package com.golajugaenyang.order.adapter.out.persistence.order.dto;

import com.golajugaenyang.order.domain.order.OrderItemStatus;
import com.golajugaenyang.order.domain.order.OrderStatus;
import java.time.OffsetDateTime;

public record ConfirmedPurchaseItemProjection(
    Long productId,
    Long orderId,
    Long orderItemId,
    OrderStatus orderStatus,
    OrderItemStatus itemStatus,
    OffsetDateTime confirmedAt
) {

}
