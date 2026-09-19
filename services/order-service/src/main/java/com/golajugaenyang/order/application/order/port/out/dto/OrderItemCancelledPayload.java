package com.golajugaenyang.order.application.order.port.out.dto;

public record OrderItemCancelledPayload(
    Long orderId,
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
