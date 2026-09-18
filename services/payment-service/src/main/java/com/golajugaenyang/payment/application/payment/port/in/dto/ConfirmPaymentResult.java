package com.golajugaenyang.payment.application.payment.port.in.dto;

import com.golajugaenyang.payment.domain.payment.Payment;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ConfirmPaymentResult(
    Long paymentId,
    String orderNumber,
    String paymentStatus,
    BigDecimal amount,
    String method,
    OffsetDateTime approvedAt
) {

    public static ConfirmPaymentResult from(Payment payment) {
        return new ConfirmPaymentResult(
            payment.getId(),
            payment.getOrderNumber(),
            payment.getPaymentStatus().name(),
            payment.getAmount(),
            payment.getMethod(),
            payment.getApprovedAt()
        );
    }
}
