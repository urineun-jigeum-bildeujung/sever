package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import com.golajugaenyang.order.domain.order.OrderItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderItemReservationMapper {

    public static ReservationItem toReservationItem(OrderItem item) {
        boolean isTimeDeal = item.getDealItemId() != null;
        String subjectType = isTimeDeal ? "TIME_DEAL_ITEM" : "PRODUCT";
        Long subjectId = isTimeDeal ? item.getDealItemId() : item.getProductId();
        return new ReservationItem(item.getId(), subjectType, subjectId, item.getQuantity());
    }
}
