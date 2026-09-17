package com.golajugaenyang.order.adapter.out.event;

import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.order.adapter.out.persistence.ouxbox.OrderOutboxJpaRepository;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxPublisher {

    private static final long SEND_TIMEOUT_SECONDS = 5;
    private final OrderOutboxJpaRepository outboxJpaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper kafkaObjectMapper;

    @Transactional
    public void claimAndPublish(Long outboxId) {
        int claimed = outboxJpaRepository.claim(
            outboxId, OffsetDateTime.now(), OutboxStatus.PENDING);
        if (claimed == 0) {
            return;
        }

        OrderOutbox outbox = outboxJpaRepository.findById(outboxId)
            .orElseThrow(() ->
                new IllegalStateException("선점한 outbox row를 찾을 수 없습니다. id=" + outboxId));

        String payload;
        try {
            payload = kafkaObjectMapper.writeValueAsString(outbox.getPayload());
        } catch (Exception e) {
            log.error("[OrderOutbox] 직렬화 실패. outboxId={}", outboxId, e);
            outbox.markFailed();
            return;
        }

        try {
            kafkaTemplate.send(
                outbox.getEventType(),
                String.valueOf(outbox.getAggregateId()),
                payload
            ).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            outbox.markSent();
        } catch (TimeoutException | ExecutionException e) {
            throw new IllegalStateException("Kafka 발행 실패, 다음 폴링에서 재시도됩니다. outboxId=" + outboxId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka 발행 중 인터럽트됨. outboxId=" + outboxId, e);
        }
    }
}
