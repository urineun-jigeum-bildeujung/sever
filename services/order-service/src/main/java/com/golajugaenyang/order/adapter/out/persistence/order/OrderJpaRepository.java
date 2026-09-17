package com.golajugaenyang.order.adapter.out.persistence.order;

import com.golajugaenyang.order.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    boolean existsByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    Order findByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);
}
