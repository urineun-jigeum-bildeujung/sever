package com.golajugaenyang.payment.adapter.in.event.dto;

public record OrderItemCancelledMessage(
    Long orderId,
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
