package com.golajugaenyang.order.adapter.in.web.cart.dto;

import com.golajugaenyang.order.domain.cart.CartItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
    @NotNull CartItemType itemType,
    @NotNull Long itemId,
    @Min(1) @Max(99) int quantity
) {

}
