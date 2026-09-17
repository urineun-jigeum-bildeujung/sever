package com.golajugaenyang.order.adapter.out.event;


import com.golajugaenyang.order.adapter.out.persistence.ouxbox.OrderOutboxJpaRepository;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderOutboxStatusUpdater {

    private final OrderOutboxJpaRepository outboxJpaRepository;

    @Transactional
    public void markSent(Long outboxId) {
        outboxJpaRepository.findById(outboxId).ifPresent(OrderOutbox::markSent);
    }

    @Transactional
    public void markFailed(Long outboxId) {
        outboxJpaRepository.findById(outboxId).ifPresent(OrderOutbox::markFailed);
    }
}
