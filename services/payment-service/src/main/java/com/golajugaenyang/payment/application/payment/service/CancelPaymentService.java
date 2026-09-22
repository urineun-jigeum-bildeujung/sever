package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.payment.application.payment.port.in.CancelPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.out.TossPaymentGatewayPort;
import com.golajugaenyang.payment.domain.payment.Payment;
import com.golajugaenyang.payment.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CancelPaymentService implements CancelPaymentUseCase {

    private final CancelPaymentTransactionSupport transactionSupport;
    private final TossPaymentGatewayPort tossPaymentGatewayPort;

    @Override
    public void cancelPaymentForOrder(Long orderId) {
        // Phase 1: 취소 의도 기록 (로컬 트랜잭션)
        Payment payment = transactionSupport.beginCancellation(orderId);

        if (payment == null || payment.getPaymentStatus() != PaymentStatus.CANCELLING) {
            return;
        }

        // Phase 2: 원격 취소 (트랜잭션 밖)
        tossPaymentGatewayPort.cancel(
            payment.getPaymentKey(), payment.getCancelReason(), payment.getCancelIdempotencyKey());

        // Phase 3: 로컬 확정 (원격 호출 없음)
        transactionSupport.finalizeCancellation(payment.getId());
    }
}
