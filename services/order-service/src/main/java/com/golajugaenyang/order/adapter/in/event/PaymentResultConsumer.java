package com.golajugaenyang.order.adapter.in.event;


import com.golajugaenyang.order.adapter.in.event.dto.PaymentResultMessage;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.domain.order.Order;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final String TEMPORARY_DEFAULT_PAYMENT_METHOD = "토스페이먼츠";

    private final OrderRepositoryPort orderRepositoryPort;
    private final ObjectMapper kafkaObjectMapper;

    @KafkaListener(topics = "payment.completed", groupId = "order-service.order-status-consumer")
    @Transactional
    public void onPaymentCompleted(String rawMessage) {
        PaymentResultMessage message = kafkaObjectMapper
            .readValue(rawMessage, PaymentResultMessage.class);
        Order order = orderRepositoryPort.findByOrderItemIdOrNull(message.orderItemId());
        if (order == null) {
            log.error("[PaymentCompleted] orderItemId에 대응하는 주문을 찾을 수 없습니다. orderItemId={}",
                message.orderItemId());
            return;
        }
        order.markPaid(OffsetDateTime.now(), TEMPORARY_DEFAULT_PAYMENT_METHOD);
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service.order-status-consumer")
    @Transactional
    public void onPaymentFailed(String rawMessage) {
        PaymentResultMessage message = kafkaObjectMapper
            .readValue(rawMessage, PaymentResultMessage.class);
        Order order = orderRepositoryPort.findByOrderItemIdOrNull(message.orderItemId());
        if (order == null) {
            log.error("[PaymentFailed] orderItemId에 대응하는 주문을 찾을 수 없습니다. orderItemId={}",
                message.orderItemId());
            return;
        }
        order.cancelDueToPaymentFailure();
    }
}
