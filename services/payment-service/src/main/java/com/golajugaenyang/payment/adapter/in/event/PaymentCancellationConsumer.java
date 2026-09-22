package com.golajugaenyang.payment.adapter.in.event;


import com.golajugaenyang.payment.adapter.in.event.dto.OrderItemCancelledMessage;
import com.golajugaenyang.payment.application.payment.port.in.CancelPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentCancellationConsumer {

    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final ObjectMapper kafkaObjectMapper;

    @KafkaListener(topics = "order.item-cancelled", groupId = "payment-service.refund-consumer")
    public void onOrderItemCancelled(String rawMessage) {
        OrderItemCancelledMessage message = kafkaObjectMapper
            .readValue(rawMessage, OrderItemCancelledMessage.class);
        cancelPaymentUseCase.cancelPaymentForOrder(message.orderId());
    }

}
