package com.golajugaenyang.product.application.inventory.port.in.dto;

import com.golajugaenyang.product.domain.inventory.StockSubjectType;

public record ReserveItemCommand(
    Long orderItemId,
    StockSubjectType subjectType,
    Long subjectId,
    int quantity
) {

}
