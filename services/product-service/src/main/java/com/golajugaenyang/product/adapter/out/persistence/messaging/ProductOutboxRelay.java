package com.golajugaenyang.product.adapter.out.persistence.messaging;


import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.product.adapter.out.persistence.outbox.ProductOutboxJpaRepository;
import com.golajugaenyang.product.domain.outbox.ProductOutbox;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProductOutboxRelay {

    private static final int BATCH_SIZE = 100;

    private final ProductOutboxJpaRepository productOutboxJpaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void relay() {
        List<ProductOutbox> pending = productOutboxJpaRepository.findByStatusInOrderByCreatedAtAsc(
            List.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
            PageRequest.of(0, BATCH_SIZE));

        for (ProductOutbox outbox : pending) {
            try {
                String topic = resolveTopic(outbox.getEventType());
                kafkaTemplate.send(topic, String.valueOf(outbox.getAggregateId()),
                    outbox.getPayload().toString()).get();
                outbox.markSent();
            } catch (Exception e) {
                log.warn("[ProductOutboxRelay] 발행 실패 id={}, eventType={}",
                    outbox.getId(), outbox.getEventType(), e);
                outbox.markFailed();
            }
        }
    }

    private String resolveTopic(String eventType) {
        return switch (eventType) {
            case "PRODUCT_AVAILABILITY_CHANGED" -> "product.availability-changed";
            default -> throw new IllegalStateException("알 수 없는 이벤트 타입: " + eventType);
        };
    }
}
