package com.golajugaenyang.product.adapter.in.messaging;


import com.golajugaenyang.product.adapter.in.messaging.dto.PaymentResultMessage;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final InventoryCommandUseCase inventoryCommandUseCase;

    @KafkaListener(topics = "payment.failed", groupId = "product-service.inventory-consumer")
    public void onMessage(PaymentResultMessage message) {
        inventoryCommandUseCase.release(
            message.subjectType(), message.subjectId(), message.orderItemId(), message.quantity());
    }
}
