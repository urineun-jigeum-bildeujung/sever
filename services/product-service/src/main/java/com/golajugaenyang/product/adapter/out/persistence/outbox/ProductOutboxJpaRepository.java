package com.golajugaenyang.product.adapter.out.persistence.outbox;

import com.golajugaenyang.common.jpa.entity.OutboxStatus;
import com.golajugaenyang.product.domain.outbox.ProductOutbox;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOutboxJpaRepository extends JpaRepository<ProductOutbox, Long> {

    List<ProductOutbox> findByStatusInOrderByCreatedAtAsc(
        List<OutboxStatus> statuses, Pageable pageable);
}
