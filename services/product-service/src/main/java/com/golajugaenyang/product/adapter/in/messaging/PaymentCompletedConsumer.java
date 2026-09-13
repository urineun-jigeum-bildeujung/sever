package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.PaymentResultMessage;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.completed", groupId = "product-service.inventory-consumer")
    public void onMessage(String rawMessage) {
        PaymentResultMessage message =
            objectMapper.readValue(rawMessage, PaymentResultMessage.class);
        inventoryCommandUseCase.confirm(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
