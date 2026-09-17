package com.golajugaenyang.order.adapter.out.event;


import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.order.adapter.out.persistence.ouxbox.OrderOutboxJpaRepository;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxPoller {

    private static final int BATCH_SIZE = 100;

    private final OrderOutboxJpaRepository outboxJpaRepository;
    private final OrderOutboxPublisher publisher;

    @Scheduled(fixedDelay = 1000)
    public void publishPending() {
        List<Long> candidateIds = outboxJpaRepository
            .findByStatusAndClaimedAtIsNullOrderByCreatedAtAsc(
                OutboxStatus.PENDING, PageRequest.of(0, BATCH_SIZE))
            .stream().map(OrderOutbox::getId).toList();

        for (Long id : candidateIds) {
            try {
                publisher.claimAndPublish(id);
            } catch (RuntimeException e) {
                log.warn("[OrderOutbox] 발행 재시도 대상. outboxId={}, reason={}", id, e.getMessage());
            }
        }
    }
}
