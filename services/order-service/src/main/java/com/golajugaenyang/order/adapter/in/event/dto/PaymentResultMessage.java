package com.golajugaenyang.order.adapter.in.event.dto;

public record PaymentResultMessage(
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
