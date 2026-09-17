package com.golajugaenyang.payment.adapter.out.external.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaymentContextResponse(
    Long orderId,
    String orderNumber,
    Long memberId,
    String orderStatus,
    BigDecimal totalAmount,
    String orderName,
    List<OrderPaymentItemResponse> items
) {

}
