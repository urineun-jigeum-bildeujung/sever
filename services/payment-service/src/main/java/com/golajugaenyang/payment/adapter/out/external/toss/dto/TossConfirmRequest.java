package com.golajugaenyang.payment.adapter.out.external.toss.dto;

import java.math.BigDecimal;

public record TossConfirmRequest(
    String paymentKey,
    String orderId,
    BigDecimal amount
) {

}
