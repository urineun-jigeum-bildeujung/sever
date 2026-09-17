package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderCommand;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;

public interface CreateOrderUseCase {

    CreateOrderResult createOrder(CreateOrderCommand command);
}
