package com.golajugaenyang.order.application.cart.port.in;

import com.golajugaenyang.order.application.cart.port.in.dto.AddCartItemCommand;


public interface AddCartItemUseCase {

    void addItem(AddCartItemCommand command);
}
