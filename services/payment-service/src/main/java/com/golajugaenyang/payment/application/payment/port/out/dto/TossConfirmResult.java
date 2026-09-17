package com.golajugaenyang.payment.application.payment.port.out.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TossConfirmResult(
    String paymentKey,
    String orderId,
    BigDecimal totalAmount,
    String method,
    String status,
    OffsetDateTime approvedAt
) {

}
