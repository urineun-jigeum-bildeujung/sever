package com.golajugaenyang.order.adapter.out.persistence.claim;

import com.golajugaenyang.order.domain.claim.OrderClaim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderClaimJpaRepository extends JpaRepository<OrderClaim, Long> {

    @Query("select distinct c from OrderClaim c left join fetch c.items where c.orderId = :orderId")
    List<OrderClaim> findByOrderId(@Param("orderId") Long orderId);
}
