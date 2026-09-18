package com.golajugaenyang.payment.application.payment.port.out.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaymentContext(
    Long orderId,
    String orderNumber,
    Long memberId,
    String orderStatus,
    BigDecimal totalAmount,
    String orderName,
    List<OrderItemSubject> items
) {

    public record OrderItemSubject(
        Long orderItemId,
        String subjectType,
        Long subjectId,
        int quantity
    ) {

    }
}
