package com.golajugaenyang.order.application.order.port.in.dto;

import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderResult(
    Long orderId,
    String orderNumber,
    String orderStatus,
    BigDecimal productAmount,
    BigDecimal shippingFee,
    BigDecimal totalAmount,
    boolean newlyCreated,
    List<ItemResult> items
) {

    public record ItemResult(
        Long orderItemId,
        String productName,
        int quantity,
        BigDecimal unitPrice
    ) {

        public static ItemResult from(OrderItem item) {
            return new ItemResult(
                item.getId(),
                item.getProductNameSnapshot(),
                item.getQuantity(),
                item.getUnitPrice()
            );
        }
    }

    public static CreateOrderResult from(
        Order order,
        boolean newlyCreated
    ) {
        List<ItemResult> items = order.getItems().stream()
            .map(ItemResult::from)
            .toList();
        return new CreateOrderResult(
            order.getId(),
            order.getOrderNumber(),
            order.getOrderStatus().name(),
            order.getProductAmount(),
            order.getShippingFee(),
            order.getTotalAmount(),
            newlyCreated,
            items
        );
    }
}
