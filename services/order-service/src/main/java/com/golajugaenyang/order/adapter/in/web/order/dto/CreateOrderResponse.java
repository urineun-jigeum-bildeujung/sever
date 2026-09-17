package com.golajugaenyang.order.adapter.in.web.order.dto;

import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderResponse(
    Long orderId,
    String orderNumber,
    String orderStatus,
    BigDecimal productAmount,
    BigDecimal shippingFee,
    BigDecimal totalAmount,
    List<ItemResponse> items
) {

    public record ItemResponse(
        Long orderItemId, String productName, int quantity, BigDecimal unitPrice) {

        public static ItemResponse from(CreateOrderResult.ItemResult item) {
            return new ItemResponse(
                item.orderItemId(),
                item.productName(),
                item.quantity(),
                item.unitPrice()
            );
        }
    }

    public static CreateOrderResponse from(CreateOrderResult result) {
        List<ItemResponse> items = result.items().stream()
            .map(ItemResponse::from).toList();
        return new CreateOrderResponse(
            result.orderId(),
            result.orderNumber(),
            result.orderStatus(),
            result.productAmount(),
            result.shippingFee(),
            result.totalAmount(),
            items
        );
    }
}