package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.PaymentResultMessage;
import com.golajugaenyang.product.adapter.in.messaging.support.KafkaMessageMapper;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;
    private final KafkaMessageMapper kafkaMessageMapper;

    @KafkaListener(topics = "payment.failed", groupId = "product-service.inventory-consumer")
    public void onMessage(String rawMessage) {
        PaymentResultMessage message = kafkaMessageMapper.readValue(
            rawMessage, PaymentResultMessage.class);
        inventoryCommandUseCase.release(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
