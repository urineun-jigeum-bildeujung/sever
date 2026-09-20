package com.golajugaenyang.order.application.order.port.in.dto;

import java.time.OffsetDateTime;

public record GetOrderListQuery(
    Long memberId,
    OffsetDateTime cursorOrderedAt,
    Long cursorOrderId,
    int size
) {

}
