package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.payment.application.payment.port.in.CancelPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.out.PaymentRepositoryPort;
import com.golajugaenyang.payment.application.payment.port.out.TossPaymentGatewayPort;
import com.golajugaenyang.payment.domain.payment.Payment;
import com.golajugaenyang.payment.domain.payment.PaymentStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPaymentService implements CancelPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final TossPaymentGatewayPort tossPaymentGatewayPort;

    @Override
    @Transactional
    public void cancelPaymentForOrder(Long orderId) {
        Optional<Payment> maybePayment = paymentRepositoryPort.findByOrderId(orderId);
        if (maybePayment.isEmpty()) {
            return;
        }
        Payment payment = maybePayment.get();
        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            return;
        }
        tossPaymentGatewayPort.cancel(payment.getPaymentKey(), "주문 취소로 인한 결제 취소");
        payment.cancel("주문 취소");
    }
}
