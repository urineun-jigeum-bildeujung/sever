package com.golajugaenyang.order.application.claim.port.in.dto;

import java.util.List;

public record CreateClaimCommand(
    Long orderId,
    Long memberId,
    String claimType,
    String reason,
    List<Item> items,
    List<String> imageUrls
) {

    public record Item(
        Long orderItemId,
        int quantity
    ) {

    }
}
