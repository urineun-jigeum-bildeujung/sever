package com.golajugaenyang.payment.application.payment.port.in;

public interface CancelPaymentUseCase {

    void cancelPaymentForOrder(Long orderId);
}
