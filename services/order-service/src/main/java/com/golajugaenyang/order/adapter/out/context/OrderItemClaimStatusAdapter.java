package com.golajugaenyang.order.adapter.out.context;


import com.golajugaenyang.order.adapter.out.persistence.order.OrderItemJpaRepository;
import com.golajugaenyang.order.application.claim.port.out.OrderItemClaimStatusPort;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemClaimStatusAdapter implements OrderItemClaimStatusPort {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public List<Long> claimForNewRequest(List<Long> orderItemIds, String claimStatus) {
        List<Long> sortedIds = orderItemIds.stream().sorted().toList();
        List<Long> failed = new ArrayList<>();
        for (Long orderItemId : sortedIds) {
            int updated =
                orderItemJpaRepository.claimForNewRequest(orderItemId, claimStatus);
            if (updated == 0) {
                failed.add(orderItemId);
            }
        }
        return failed;
    }
}
