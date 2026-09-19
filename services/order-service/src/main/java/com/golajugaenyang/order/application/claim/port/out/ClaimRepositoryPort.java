package com.golajugaenyang.order.application.claim.port.out;

import com.golajugaenyang.order.domain.claim.OrderClaim;
import java.util.List;

public interface ClaimRepositoryPort {

    OrderClaim save(OrderClaim claim);

    boolean existsActiveClaimForItems(Long orderId, List<Long> orderItemIds);
}
