package com.golajugaenyang.payment.application.payment.port.out;

import com.golajugaenyang.payment.application.payment.port.out.dto.TossConfirmResult;
import java.math.BigDecimal;

public interface TossPaymentGatewayPort {

    TossConfirmResult confirm(String paymentKey, String orderId, BigDecimal amount);

    void cancel(String paymentKey, String cancelReason);
}
