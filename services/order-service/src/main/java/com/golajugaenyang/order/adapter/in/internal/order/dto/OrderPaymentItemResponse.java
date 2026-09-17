package com.golajugaenyang.order.adapter.in.internal.order.dto;

import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;

public record OrderPaymentItemResponse(
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

    public static OrderPaymentItemResponse from(ReservationItem item) {
        return new OrderPaymentItemResponse(
            item.orderItemId(),
            item.subjectType(),
            item.subjectId(),
            item.quantity()
        );
    }
}
