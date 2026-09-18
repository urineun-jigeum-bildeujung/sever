package com.golajugaenyang.payment.adapter.out.external.order.dto;

public record OrderPaymentItemResponse(
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
