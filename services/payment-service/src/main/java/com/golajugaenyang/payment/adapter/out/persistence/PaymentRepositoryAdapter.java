package com.golajugaenyang.payment.adapter.out.persistence;


import com.golajugaenyang.payment.application.payment.port.out.PaymentRepositoryPort;
import com.golajugaenyang.payment.domain.payment.Payment;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return paymentJpaRepository.findById(id).orElseThrow(() ->
            new IllegalStateException("결제 정보를 찾을 수 없습니다. id=" + id));
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<Payment> findByOrderNumber(String orderNumber) {
        return paymentJpaRepository.findByOrderNumber(orderNumber);
    }
}
