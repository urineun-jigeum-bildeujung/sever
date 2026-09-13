package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.OrderItemReturnedMessage;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemReturnedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;

    @KafkaListener(topics = "order.item-returned", groupId = "product-service.inventory-consumer")
    public void onMessage(OrderItemReturnedMessage message) {
        inventoryCommandUseCase.restore(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
