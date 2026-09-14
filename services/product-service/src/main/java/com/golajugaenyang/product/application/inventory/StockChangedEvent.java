package com.golajugaenyang.product.application.inventory;

import com.golajugaenyang.product.domain.inventory.StockSubjectType;

public record StockChangedEvent(
    StockSubjectType subjectType,
    Long subjectId
) {

}
