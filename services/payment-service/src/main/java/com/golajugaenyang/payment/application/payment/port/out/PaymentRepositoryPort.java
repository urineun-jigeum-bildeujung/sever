package com.golajugaenyang.payment.application.payment.port.out;

import com.golajugaenyang.payment.domain.payment.Payment;
import java.util.Optional;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Payment findById(Long id);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByOrderNumber(String orderNumber);
}
