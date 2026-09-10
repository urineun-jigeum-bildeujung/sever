package com.golajugaenyang.order.domain.order;

import lombok.Getter;

@Getter
public enum OrderItemStatus {
    PAID,
    CANCELLED,
    PARTIAL_RETURN,
    RETURNED
}
