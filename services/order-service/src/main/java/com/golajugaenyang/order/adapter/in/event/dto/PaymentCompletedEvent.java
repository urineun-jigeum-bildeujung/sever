package com.golajugaenyang.order.adapter.in.event.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentCompletedEvent(
    Long orderId,
    String orderNumber,
    String paymentKey,
    BigDecimal approvedAmount,
    OffsetDateTime approvedAt
) {

}
