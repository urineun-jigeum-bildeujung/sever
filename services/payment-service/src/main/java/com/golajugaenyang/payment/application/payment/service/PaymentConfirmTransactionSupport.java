package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentResult;
import com.golajugaenyang.payment.application.payment.port.out.EventOutboxPort;
import com.golajugaenyang.payment.application.payment.port.out.PaymentRepositoryPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;
import com.golajugaenyang.payment.application.payment.port.out.dto.TossConfirmResult;
import com.golajugaenyang.payment.domain.payment.Payment;
import com.golajugaenyang.payment.domain.payment.PaymentStatus;
import com.golajugaenyang.payment.error.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentConfirmTransactionSupport {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final EventOutboxPort eventOutboxPort;

    @Transactional(readOnly = true)
    public Payment findConfirmable(String orderNumber, Long memberId) {
        Payment payment = paymentRepositoryPort.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getMemberId().equals(memberId)) {
            throw new AppException(PaymentErrorCode.ORDER_OWNER_MISMATCH);
        }

        if (payment.getPaymentStatus() != PaymentStatus.READY
            && payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new AppException(PaymentErrorCode.PAYMENT_NOT_CONFIRMABLE);
        }
        return payment;
    }

    @Transactional
    public void recordApproved(Long paymentId, TossConfirmResult tossResult) {
        Payment payment = paymentRepositoryPort.findById(paymentId);
        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            return;
        }
        payment.recordApproval(
            tossResult.paymentKey(), tossResult.method(), tossResult.approvedAt());
    }

    @Transactional
    public ConfirmPaymentResult finalizeApproved(Long paymentId, OrderPaymentContext orderContext) {
        Payment payment = paymentRepositoryPort.findById(paymentId);
        if (payment.getPaymentStatus() == PaymentStatus.DONE) {
            return ConfirmPaymentResult.from(payment);
        }
        payment.finalizeApproval();
        enqueueItemEvents(orderContext, "payment.completed");
        return ConfirmPaymentResult.from(payment);
    }

    @Transactional
    public void recordFailure(
        Long paymentId, String paymentKey, String reason, OrderPaymentContext orderContext) {
        Payment payment = paymentRepositoryPort.findById(paymentId);
        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            return;
        }
        payment.confirmFailure(paymentKey, reason);
        enqueueItemEvents(orderContext, "payment.failed");
    }

    private void enqueueItemEvents(OrderPaymentContext orderContext, String eventType) {
        for (OrderPaymentContext.OrderItemSubject item : orderContext.items()) {
            eventOutboxPort.enqueue(item.orderItemId(), eventType,
                new PaymentResultPayload(
                    item.orderItemId(), item.subjectType(),
                    item.subjectId(), item.quantity()));
        }
    }
}
