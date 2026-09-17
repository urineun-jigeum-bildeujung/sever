package com.golajugaenyang.order.adapter.in.internal.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.OrderPaymentContextResult;
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

    public static OrderPaymentContextResponse from(OrderPaymentContextResult result) {
        List<OrderPaymentItemResponse> items = result.items().stream()
            .map(OrderPaymentItemResponse::from)
            .toList();
        return new OrderPaymentContextResponse(
            result.orderId(),
            result.orderNumber(),
            result.memberId(),
            result.orderStatus(),
            result.totalAmount(),
            result.orderName(),
            items
        );
    }
}
