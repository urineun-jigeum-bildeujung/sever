package com.golajugaenyang.order.application.claim.port.out.dto;

import java.util.List;

public record ClaimableOrder(
    Long orderId,
    boolean claimable,
    List<Item> items
) {

    public record Item(
        Long orderItemId,
        int effectiveQuantity
    ) {

    }
}
