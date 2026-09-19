package com.golajugaenyang.order.adapter.out.context;


import com.golajugaenyang.order.adapter.out.persistence.order.OrderItemJpaRepository;
import com.golajugaenyang.order.application.claim.port.out.OrderItemClaimStatusPort;
import com.golajugaenyang.order.domain.order.OrderItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderItemClaimStatusAdapter implements OrderItemClaimStatusPort {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    @Transactional
    public void updateActiveClaimStatus(List<Long> orderItemIds, String claimStatus) {
        List<OrderItem> items = orderItemJpaRepository.findAllById(orderItemIds);
        items.forEach(item -> item.updateActiveClaimStatus(claimStatus));
    }
}
