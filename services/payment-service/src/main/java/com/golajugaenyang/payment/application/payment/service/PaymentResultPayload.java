package com.golajugaenyang.payment.application.payment.service;

public record PaymentResultPayload(
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
