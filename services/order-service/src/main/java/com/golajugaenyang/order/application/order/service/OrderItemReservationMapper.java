package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.out.dto.OrderItemCancelledPayload;
import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import com.golajugaenyang.order.domain.order.OrderItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderItemReservationMapper {

    public static ReservationItem toReservationItem(OrderItem item) {
        return new ReservationItem(
            item.getId(), subjectType(item), subjectId(item), item.getQuantity());
    }

    public static OrderItemCancelledPayload toCancelledPayload(Long orderId, OrderItem item) {
        return new OrderItemCancelledPayload(
            orderId, item.getId(), subjectType(item), subjectId(item), item.getQuantity());
    }

    private static String subjectType(OrderItem item) {
        return item.getDealItemId() != null ? "TIME_DEAL_ITEM" : "PRODUCT";
    }

    private static Long subjectId(OrderItem item) {
        return item.getDealItemId() != null ? item.getDealItemId() : item.getProductId();
    }
}
