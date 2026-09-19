package com.golajugaenyang.order.adapter.out.persistence.claim;

import com.golajugaenyang.order.application.claim.port.out.ClaimRepositoryPort;
import com.golajugaenyang.order.domain.claim.ClaimStatus;
import com.golajugaenyang.order.domain.claim.OrderClaim;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderClaimRepositoryAdapter implements ClaimRepositoryPort {

    private final OrderClaimJpaRepository orderClaimJpaRepository;

    @Override
    public OrderClaim save(OrderClaim claim) {
        return orderClaimJpaRepository.save(claim);
    }

    @Override
    public boolean existsActiveClaimForItems(Long orderId, List<Long> orderItemIds) {
        return orderClaimJpaRepository.existsActiveClaimForItems(
            orderId, orderItemIds, ClaimStatus.terminalStates());
    }
}
