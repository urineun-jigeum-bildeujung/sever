package com.golajugaenyang.order.adapter.out.event;


import com.golajugaenyang.order.adapter.out.persistence.ouxbox.OrderOutboxJpaRepository;
import com.golajugaenyang.order.application.order.port.out.EventOutboxPort;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OrderEventOutboxAdapter implements EventOutboxPort {

    private final OrderOutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper kafkaObjectMapper;

    @Override
    public void enqueue(Long aggregateId, String eventType, Object payload) {
        JsonNode payloadNode = kafkaObjectMapper.valueToTree(payload);
        outboxJpaRepository.save(
            OrderOutbox.create("ORDER", aggregateId, eventType, payloadNode));
    }
}
