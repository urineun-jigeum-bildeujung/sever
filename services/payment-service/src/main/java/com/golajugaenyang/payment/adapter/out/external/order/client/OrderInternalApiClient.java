package com.golajugaenyang.payment.adapter.out.external.order.client;

import com.golajugaenyang.payment.adapter.out.external.order.dto.OrderPaymentContextResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface OrderInternalApiClient {

    @GetExchange("/internal/orders/{orderId}")
    OrderPaymentContextResponse getPaymentContext(@PathVariable("orderId") Long orderId);
}
