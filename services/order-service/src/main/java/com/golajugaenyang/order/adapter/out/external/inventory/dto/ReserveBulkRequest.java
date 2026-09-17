package com.golajugaenyang.order.adapter.out.external.inventory.dto;

import java.util.List;

public record ReserveBulkRequest(
    List<Item> items
) {

    public record Item(
        Long orderItemId,
        String subjectType,
        Long subjectId,
        int quantity
    ) {

    }
}
