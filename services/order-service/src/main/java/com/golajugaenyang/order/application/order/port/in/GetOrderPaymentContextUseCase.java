package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.OrderPaymentContextResult;

public interface GetOrderPaymentContextUseCase {

    OrderPaymentContextResult getPaymentContext(Long orderId);
}
