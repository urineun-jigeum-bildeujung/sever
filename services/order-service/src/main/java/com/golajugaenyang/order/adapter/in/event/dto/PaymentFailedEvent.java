package com.golajugaenyang.order.adapter.in.event.dto;

public record PaymentFailedEvent(
    Long orderId,
    String orderNumber,
    String reason
) {

}
