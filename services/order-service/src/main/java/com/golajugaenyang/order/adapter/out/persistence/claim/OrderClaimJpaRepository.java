package com.golajugaenyang.order.adapter.out.persistence.claim;

import com.golajugaenyang.order.domain.claim.ClaimStatus;
import com.golajugaenyang.order.domain.claim.OrderClaim;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderClaimJpaRepository extends JpaRepository<OrderClaim, Long> {

    @Query("""
        select count(ci) > 0 from OrderClaimItem ci
        where ci.claim.orderId = :orderId
          and ci.orderItemId in :orderItemIds
          and ci.claim.claimStatus not in :terminalStatuses
        """)
    boolean existsActiveClaimForItems(
        @Param("orderId") Long orderId,
        @Param("orderItemIds") List<Long> orderItemIds,
        @Param("terminalStatuses") Set<ClaimStatus> terminalStatuses);

}
