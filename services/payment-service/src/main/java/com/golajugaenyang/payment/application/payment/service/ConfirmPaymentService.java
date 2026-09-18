package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.payment.application.payment.port.in.ConfirmPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentCommand;
import com.golajugaenyang.payment.application.payment.port.in.dto.ConfirmPaymentResult;
import com.golajugaenyang.payment.application.payment.port.out.OrderLookupPort;
import com.golajugaenyang.payment.application.payment.port.out.TossPaymentGatewayPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;
import com.golajugaenyang.payment.application.payment.port.out.dto.TossConfirmResult;
import com.golajugaenyang.payment.domain.payment.Payment;
import com.golajugaenyang.payment.domain.payment.PaymentStatus;
import com.golajugaenyang.payment.error.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private final PaymentConfirmTransactionSupport transactionSupport;
    private final OrderLookupPort orderLookupPort;
    private final TossPaymentGatewayPort tossPaymentGatewayPort;

    @Override
    public ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command) {
        // Phase 1: 로컬 조회
        Payment payment = transactionSupport.findConfirmable(command.orderId(), command.memberId());

        if (command.amount().compareTo(payment.getAmount()) != 0) {
            throw new AppException(PaymentErrorCode.AMOUNT_MISMATCH);
        }

        if (payment.getPaymentStatus() == PaymentStatus.DONE) {
            return ConfirmPaymentResult.from(payment);
        }

        OrderPaymentContext orderContext = orderLookupPort.lookup(payment.getOrderId());
        if (payment.getPaymentStatus() == PaymentStatus.APPROVED) {
            return transactionSupport.finalizeApproved(payment.getId(), orderContext);
        }

        // Phase 2: 원격 결제 승인
        TossConfirmResult tossResult;
        try {
            tossResult = tossPaymentGatewayPort
                .confirm(command.paymentKey(), command.orderId(), payment.getAmount());
        } catch (AppException e) {
            if (e.getErrorCode() == PaymentErrorCode.TOSS_CONFIRM_FAILED) {
                transactionSupport.recordFailure(
                    payment.getId(), command.paymentKey(), e.getMessage(), orderContext);
            }
            throw e;
        }

        if (!"DONE".equals(tossResult.status())) {
            throw new AppException(PaymentErrorCode.TOSS_SERVICE_UNAVAILABLE);
        }

        if (tossResult.totalAmount().compareTo(payment.getAmount()) != 0) {
            tossPaymentGatewayPort.cancel(
                tossResult.paymentKey(), "금액 불일치로 인한 자동 취소");
            transactionSupport.recordFailure(
                payment.getId(), tossResult.paymentKey(), "승인 응답 금액 불일치", orderContext);
            throw new AppException(PaymentErrorCode.AMOUNT_MISMATCH);
        }

        // Phase 3: 로컬 커밋
        transactionSupport.recordApproved(payment.getId(), tossResult);
        return transactionSupport.finalizeApproved(payment.getId(), orderContext);
    }
}