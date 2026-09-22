package com.golajugaenyang.payment.adapter.in.web.payment.dto;

import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentResult;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ConfirmPaymentResponse(
    Long paymentId,
    Long orderId,
    String orderNumber,
    String paymentStatus,
    BigDecimal amount,
    String method,
    OffsetDateTime approvedAt
) {

    public static ConfirmPaymentResponse from(ConfirmPaymentResult result) {
        return new ConfirmPaymentResponse(
            result.paymentId(),
            result.orderId(),
            result.orderNumber(),
            result.paymentStatus(),
            result.amount(),
            result.method(),
            result.approvedAt()
        );
    }
}
