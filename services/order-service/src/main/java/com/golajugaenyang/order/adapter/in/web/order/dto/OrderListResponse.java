package com.golajugaenyang.order.adapter.in.web.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.OrderListResult;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderListResponse(
    List<OrderSummary> orders,
    String nextCursor,
    boolean hasNext
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
        String thumbnailUrl,
        String productName,
        int quantity
    ) {

    }

    public static OrderListResponse from(
        OrderListResult result, String nextCursor) {
        List<OrderSummary> orders = result.orders().stream()
            .map(o -> new OrderSummary(
                o.orderId(), o.orderNumber(),
                o.orderedAt(), o.orderStatus(),
                o.totalAmount(),
                o.items().stream()
                    .map(i -> new ItemSummary(
                        i.orderItemId(), i.thumbnailUrl(),
                        i.productName(), i.quantity()))
                    .toList()))
            .toList();
        return new OrderListResponse(
            orders, nextCursor, result.hasNext());
    }

}
