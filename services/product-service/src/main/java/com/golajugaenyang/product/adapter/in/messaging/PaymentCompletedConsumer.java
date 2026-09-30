package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.PaymentResultMessage;
import com.golajugaenyang.product.adapter.in.messaging.support.KafkaMessageMapper;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;
    private final KafkaMessageMapper kafkaMessageMapper;

    @KafkaListener(topics = "payment.completed", groupId = "product-service.inventory-consumer")
    public void onMessage(String rawMessage) {
        PaymentResultMessage message = kafkaMessageMapper.readValue(
            rawMessage, PaymentResultMessage.class);
        inventoryCommandUseCase.confirm(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
