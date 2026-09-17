package com.golajugaenyang.payment.adapter.out.persistence;

import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.payment.domain.payment.PaymentOutbox;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {

    List<PaymentOutbox> findByStatusAndClaimedAtIsNullOrderByCreatedAtAsc(
        OutboxStatus status, Pageable pageable);

    @Modifying
    @Query("""
        update PaymentOutbox o set o.claimedAt = :now
        where o.id = :id and o.status = :pending and o.claimedAt is null
        """)
    int claim(
        @Param("id") Long id,
        @Param("now") OffsetDateTime now,
        @Param("pending") OutboxStatus pending);
}
