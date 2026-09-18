package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.payment.application.payment.port.in.RequestPaymentUseCase;
import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentCommand;
import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentResult;
import com.golajugaenyang.payment.application.payment.port.out.OrderLookupPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;
import com.golajugaenyang.payment.domain.payment.Payment;
import com.golajugaenyang.payment.error.PaymentErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestPaymentService implements RequestPaymentUseCase {

    private final OrderLookupPort orderLookupPort;
    private final PaymentRequestTransactionSupport transactionSupport;

    @Override
    public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
        OrderPaymentContext orderContext = orderLookupPort.lookup(command.orderId());

        if (!orderContext.memberId().equals(command.memberId())) {
            throw new AppException(PaymentErrorCode.ORDER_OWNER_MISMATCH);
        }
        if (!"PENDING".equals(orderContext.orderStatus())) {
            throw new AppException(PaymentErrorCode.ORDER_NOT_PAYABLE);
        }

        try {
            return transactionSupport.persistPaymentRequest(command, orderContext);
        } catch (DataIntegrityViolationException dup) {
            Payment existing = transactionSupport
                .findExistingPayment(command.orderId()).orElseThrow(() -> dup);
            return new RequestPaymentResult(
                orderContext.orderNumber(), existing.getAmount(),
                orderContext.orderName(), UUID.randomUUID().toString());
        }
    }
}
