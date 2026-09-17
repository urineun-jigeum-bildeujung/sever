package com.golajugaenyang.order.adapter.out.persistence.ouxbox;

import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.order.domain.outbox.OrderOutbox;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutbox, Long> {

    List<OrderOutbox> findByStatusAndClaimedAtIsNullOrderByCreatedAtAsc(
        OutboxStatus status, Pageable pageable
    );

    @Modifying
    @Query("""
        update OrderOutbox o set o.claimedAt = :now
        where o.id = :id and o.status = :pending and o.claimedAt is null
        """)
    int claim(
        @Param("id") Long id,
        @Param("now") OffsetDateTime now,
        @Param("pending") OutboxStatus pending);
}
