package com.golajugaenyang.payment.application.payment.service;


import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentCommand;
import com.golajugaenyang.payment.application.payment.port.in.dto.RequestPaymentResult;
import com.golajugaenyang.payment.application.payment.port.out.PaymentRepositoryPort;
import com.golajugaenyang.payment.application.payment.port.out.dto.OrderPaymentContext;
import com.golajugaenyang.payment.domain.payment.Payment;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentRequestTransactionSupport {

    private final PaymentRepositoryPort paymentRepositoryPort;

    @Transactional
    public RequestPaymentResult persistPaymentRequest(
        RequestPaymentCommand command,
        OrderPaymentContext orderContext
    ) {
        Payment payment;
        try {
            payment = paymentRepositoryPort.save(
                Payment.requestFor(
                    command.orderId(), orderContext.orderNumber(),
                    command.memberId(), orderContext.totalAmount()));
        } catch (DataIntegrityViolationException dup) {
            payment = paymentRepositoryPort
                .findByOrderId(command.orderId()).orElseThrow(() -> dup);
        }
        return new RequestPaymentResult(
            orderContext.orderNumber(), payment.getAmount(),
            orderContext.orderName(), UUID.randomUUID().toString());
    }
}
