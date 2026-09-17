package com.golajugaenyang.order.adapter.out.event;

import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper kafkaObjectMapper;
    private final OrderOutboxStatusUpdater statusUpdater;

    public void publish(OrderOutbox outbox) {
        try {
            String payload = kafkaObjectMapper.writeValueAsString(outbox.getPayload());
            kafkaTemplate.send(outbox.getEventType(), String.valueOf(outbox.getAggregateId()),
                    payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        statusUpdater.markSent(outbox.getId());
                    } else {
                        log.error("[OrderOutbox] 발행 실패, 다음 폴링에서 재시도됩니다. outboxId={}",
                            outbox.getId(), ex);
                    }
                });
        } catch (Exception e) {
            log.error("[OrderOutbox] 직렬화 실패. outboxId={}", outbox.getId(), e);
            statusUpdater.markFailed(outbox.getId());
        }
    }
}
