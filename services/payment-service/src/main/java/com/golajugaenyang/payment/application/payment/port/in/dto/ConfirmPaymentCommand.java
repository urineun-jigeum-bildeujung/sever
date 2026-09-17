package com.golajugaenyang.payment.application.payment.port.in.dto;

import java.math.BigDecimal;

public record ConfirmPaymentCommand(
    String paymentKey,
    String orderId,
    BigDecimal amount,
    Long memberId
) {

}
