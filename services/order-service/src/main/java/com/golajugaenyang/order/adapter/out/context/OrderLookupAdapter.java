package com.golajugaenyang.order.adapter.out.context;

import com.golajugaenyang.order.application.claim.port.out.OrderLookupPort;
import com.golajugaenyang.order.application.claim.port.out.dto.ClaimableOrder;
import com.golajugaenyang.order.application.order.service.OrderAccessGuard;
import com.golajugaenyang.order.domain.order.Order;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderLookupAdapter implements OrderLookupPort {

    private final OrderAccessGuard orderAccessGuard;

    @Override
    public ClaimableOrder findClaimableOrder(Long orderId, Long memberId) {
        Order order = orderAccessGuard.findOwnedOrder(orderId, memberId);
        List<ClaimableOrder.Item> items = order.getItems().stream()
            .map(i ->
                new ClaimableOrder.Item(i.getId(), i.effectiveQuantity()))
            .toList();
        return new ClaimableOrder(order.getId(), order.isClaimableForReturn(), items);
    }
}
