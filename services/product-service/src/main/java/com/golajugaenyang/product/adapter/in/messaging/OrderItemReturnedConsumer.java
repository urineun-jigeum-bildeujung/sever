package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.OrderItemReturnedMessage;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OrderItemReturnedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.item-returned", groupId = "product-service.inventory-consumer")
    public void onMessage(String rawMessage) {
        OrderItemReturnedMessage message =
            objectMapper.readValue(rawMessage, OrderItemReturnedMessage.class);
        inventoryCommandUseCase.restore(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
