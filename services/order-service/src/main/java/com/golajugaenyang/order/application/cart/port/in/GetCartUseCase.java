package com.golajugaenyang.order.application.cart.port.in;

import com.golajugaenyang.order.application.cart.port.in.dto.CartResult;


public interface GetCartUseCase {

    CartResult getCart(Long memberId);
}
