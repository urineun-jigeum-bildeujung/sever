package com.golajugaenyang.order.application.cart.port.in;

import com.golajugaenyang.order.application.cart.port.in.dto.ChangeCartItemQuantityCommand;


public interface ChangeCartItemQuantityUseCase {

    void changeQuantity(ChangeCartItemQuantityCommand command);
}
