package com.golajugaenyang.order.application.order.port.in.dto;

import java.util.List;

public record CreateOrderCommand(
    Long memberId,
    String idempotencyKey,
    Long addressId,
    List<Item> items,
    String deliveryNote
) {

    public record Item(Long productId, Long dealItemId, int quantity) {

        public boolean isTimeDeal() {
            return dealItemId != null;
        }
    }
}
