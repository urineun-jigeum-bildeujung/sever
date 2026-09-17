package com.golajugaenyang.payment.application.payment.port.in.dto;

import java.math.BigDecimal;

public record RequestPaymentResult(
    String tossOrderId,
    BigDecimal amount,
    String orderName,
    String customerKey
) {

}
