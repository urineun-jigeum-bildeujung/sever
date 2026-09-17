package com.golajugaenyang.payment.adapter.out.event;

import com.golajugaenyang.payment.adapter.out.persistence.PaymentOutboxJpaRepository;
import com.golajugaenyang.payment.application.payment.port.out.EventOutboxPort;
import com.golajugaenyang.payment.domain.payment.PaymentOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentOutboxAdapter implements EventOutboxPort {

    private final PaymentOutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper kafkaObjectMapper;

    @Override
    public void enqueue(Long aggregateId, String eventType, Object payload) {
        JsonNode payloadNode = kafkaObjectMapper.valueToTree(payload);
        outboxJpaRepository.save(PaymentOutbox.create(
            "PAYMENT", aggregateId, eventType, payloadNode));
    }
}
