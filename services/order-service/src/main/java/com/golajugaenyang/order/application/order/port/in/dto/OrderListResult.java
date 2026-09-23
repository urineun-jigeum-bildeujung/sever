package com.golajugaenyang.order.application.order.port.in.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderListResult(
    List<OrderSummary> orders,
    boolean hasNext,
    OffsetDateTime lastOrderedAt,
    Long lastOrderId
) {

    public record OrderSummary(
        Long orderId,
        String orderNumber,
        OffsetDateTime orderedAt,
        String orderStatus,
        BigDecimal totalAmount,
        List<ItemSummary> items
    ) {

    }

    public record ItemSummary(
        Long orderItemId,
        Long productId,
        String thumbnailUrl,
        String productName,
        int quantity,
        BigDecimal amount
    ) {

    }
}
