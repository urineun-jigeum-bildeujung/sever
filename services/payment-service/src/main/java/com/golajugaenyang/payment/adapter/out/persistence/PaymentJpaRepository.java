package com.golajugaenyang.payment.adapter.out.persistence;

import com.golajugaenyang.payment.domain.payment.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByOrderNumber(String orderNumber);
}
