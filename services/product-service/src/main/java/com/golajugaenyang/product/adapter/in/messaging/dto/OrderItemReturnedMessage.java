package com.golajugaenyang.product.adapter.in.messaging.dto;

import com.golajugaenyang.product.domain.inventory.StockSubjectType;

public record OrderItemReturnedMessage(
    Long orderItemId,
    StockSubjectType subjectType,
    Long subjectId,
    int quantity
) {

}
