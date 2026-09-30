package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.OrderItemCancelledMessage;
import com.golajugaenyang.product.adapter.in.messaging.support.KafkaMessageMapper;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderItemCancelledConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;
    private final KafkaMessageMapper kafkaMessageMapper;

    @KafkaListener(topics = "order.item-cancelled", groupId = "product-service.inventory-consumer")
    public void onMessage(String rawMessage) {
        OrderItemCancelledMessage message = kafkaMessageMapper.readValue(
            rawMessage, OrderItemCancelledMessage.class);
        inventoryCommandUseCase.release(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }

}
