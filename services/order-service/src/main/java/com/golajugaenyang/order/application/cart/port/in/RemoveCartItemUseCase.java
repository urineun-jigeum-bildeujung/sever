package com.golajugaenyang.order.application.cart.port.in;

import com.golajugaenyang.order.application.cart.port.in.dto.RemoveCartItemCommand;


public interface RemoveCartItemUseCase {

    void removeItem(RemoveCartItemCommand command);
}
