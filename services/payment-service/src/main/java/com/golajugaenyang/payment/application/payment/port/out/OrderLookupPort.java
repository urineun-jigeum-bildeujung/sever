package com.golajugaenyang.payment.application.payment.port.out;

import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;

public interface OrderLookupPort {

    OrderPaymentContext lookup(Long orderId);
}
