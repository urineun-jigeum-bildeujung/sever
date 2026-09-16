package com.golajugaenyang.order.adapter.in.web.cart.dto;

import com.golajugaenyang.order.application.cart.port.in.dto.CartItemResult;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CartItemResponse(
    String itemType,
    Long itemId,
    int quantity,
    boolean available,
    String unavailableReason,
    String productName,
    String thumbnailUrl,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountRate,
    BigDecimal subtotal,
    OffsetDateTime dealEndAt
) {

    public static CartItemResponse from(CartItemResult result) {
        return new CartItemResponse(
            result.itemType(), result.itemId(),
            result.quantity(), result.available(),
            result.unavailableReason(),
            result.productName(), result.thumbnailUrl(),
            result.price(), result.originalPrice(), result.discountRate(),
            result.subtotal(), result.dealEndAt()
        );
    }
}
