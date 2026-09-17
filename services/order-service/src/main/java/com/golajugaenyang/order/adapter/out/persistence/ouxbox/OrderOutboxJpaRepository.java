package com.golajugaenyang.order.adapter.out.persistence.ouxbox;

import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutbox, Long> {

    List<OrderOutbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}
