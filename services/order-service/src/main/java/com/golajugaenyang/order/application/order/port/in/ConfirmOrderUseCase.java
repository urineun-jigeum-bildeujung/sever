package com.golajugaenyang.order.application.order.port.in;

public interface ConfirmOrderUseCase {

    void confirmOrder(Long orderId, Long memberId);
}
