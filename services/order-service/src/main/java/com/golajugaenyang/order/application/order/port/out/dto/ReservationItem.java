package com.golajugaenyang.order.application.order.port.out.dto;

public record ReservationItem(
    Long orderItemId,
    String subjectType,
    Long subjectId,
    int quantity
) {

}
