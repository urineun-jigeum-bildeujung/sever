package com.golajugaenyang.order.adapter.in.internal.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.ConfirmedPurchaseItemResult;
import java.time.OffsetDateTime;

public record ConfirmedPurchaseItemResponse(
    Long productId,
    Long petId,
    Long orderId,
    Long orderItemId,
    String orderStatus,
    String itemStatus,
    OffsetDateTime confirmedAt
) {

    public static ConfirmedPurchaseItemResponse from(ConfirmedPurchaseItemResult result) {
        return new ConfirmedPurchaseItemResponse(
            result.productId(),
            result.petId(),
            result.orderId(),
            result.orderItemId(),
            result.orderStatus(),
            result.itemStatus(),
            result.confirmedAt()
        );
    }
}
