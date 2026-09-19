package com.golajugaenyang.order.application.order.port.in;

public interface CancelOrderUseCase {

    void cancelOrder(Long orderId, Long memberId);
}
