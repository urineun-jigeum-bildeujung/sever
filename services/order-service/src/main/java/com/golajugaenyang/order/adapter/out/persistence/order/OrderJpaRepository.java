package com.golajugaenyang.order.adapter.out.persistence.order;

import com.golajugaenyang.order.domain.order.Order;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    boolean existsByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    Order findByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    @Query("select o from Order o where o.memberId = :memberId order by o.orderedAt desc, o.id desc")
    List<Order> findFirstPageByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
        select o from Order o
        where o.memberId = :memberId
          and (o.orderedAt < :cursorOrderedAt
               or (o.orderedAt = :cursorOrderedAt and o.id < :cursorOrderId))
        order by o.orderedAt desc, o.id desc
        """)
    List<Order> findNextPageByMemberIdAndCursor(
        @Param("memberId") Long memberId,
        @Param("cursorOrderedAt") OffsetDateTime cursorOrderedAt,
        @Param("cursorOrderId") Long cursorOrderId,
        Pageable pageable);
}
