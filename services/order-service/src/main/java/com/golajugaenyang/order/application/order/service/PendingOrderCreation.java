package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;
import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import java.util.List;

public record PendingOrderCreation(
    Long orderId,
    List<ReservationItem> reservationItems,
    CreateOrderResult result
) {

}
