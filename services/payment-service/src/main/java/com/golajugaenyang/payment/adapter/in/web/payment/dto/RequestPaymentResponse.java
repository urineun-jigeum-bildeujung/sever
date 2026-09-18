package com.golajugaenyang.payment.adapter.in.web.payment.dto;

import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentResult;
import java.math.BigDecimal;

public record RequestPaymentResponse(
    String tossOrderId,
    BigDecimal amount,
    String orderName,
    String customerKey
) {

    public static RequestPaymentResponse from(RequestPaymentResult result) {
        return new RequestPaymentResponse(
            result.tossOrderId(),
            result.amount(),
            result.orderName(),
            result.customerKey()
        );
    }
}
