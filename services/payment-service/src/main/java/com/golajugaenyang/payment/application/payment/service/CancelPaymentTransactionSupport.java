package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.payment.application.payment.port.out.PaymentRepositoryPort;
import com.golajugaenyang.payment.domain.payment.Payment;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelPaymentTransactionSupport {

    private final PaymentRepositoryPort paymentRepositoryPort;

    @Transactional
    public Payment beginCancellation(Long orderId) {
        Optional<Payment> maybePayment = paymentRepositoryPort.findByOrderId(orderId);
        if (maybePayment.isEmpty()) {
            return null;
        }
        Payment payment = maybePayment.get();
        payment.beginCancellation(UUID.randomUUID().toString(), "주문 취소로 인한 결제 취소");
        return payment;
    }

    @Transactional
    public void finalizeCancellation(Long paymentId) {
        Payment payment = paymentRepositoryPort.findById(paymentId);
        payment.finalizeCancellation();
    }
}
