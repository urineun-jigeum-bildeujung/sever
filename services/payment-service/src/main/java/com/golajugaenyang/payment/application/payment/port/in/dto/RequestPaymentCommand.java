package com.golajugaenyang.payment.application.payment.port.in.dto;

public record RequestPaymentCommand(
    Long orderId,
    Long memberId
) {

}
