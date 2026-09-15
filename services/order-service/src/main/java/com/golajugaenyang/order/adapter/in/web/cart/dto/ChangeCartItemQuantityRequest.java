package com.golajugaenyang.order.adapter.in.web.cart.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeCartItemQuantityRequest(
    @NotNull Integer delta
) {

}
