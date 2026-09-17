package com.golajugaenyang.payment.adapter.out.external.toss.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TossConfirmResponse(
    String paymentKey,
    String orderId,
    BigDecimal totalAmount,
    String method,
    String status,
    OffsetDateTime approvedAt
) {

}
