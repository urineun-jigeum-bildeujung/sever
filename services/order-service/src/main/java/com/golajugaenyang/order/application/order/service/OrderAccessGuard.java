package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderAccessGuard {

    private final OrderRepositoryPort orderRepositoryPort;

    public Order findOwnedOrder(Long orderId, Long memberId) {
        Order order = orderRepositoryPort.findByIdOrNull(orderId);
        if (order == null) {
            throw new AppException(OrderErrorCode.ORDER_NOT_FOUND);
        }
        if (!order.getMemberId().equals(memberId)) {
            throw new AppException(OrderErrorCode.ORDER_OWNER_MISMATCH);
        }
        return order;
    }
}
