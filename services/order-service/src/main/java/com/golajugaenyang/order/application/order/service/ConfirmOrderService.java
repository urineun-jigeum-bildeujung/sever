package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.in.ConfirmOrderUseCase;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderStatus;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderAccessGuard orderAccessGuard;

    @Override
    @Transactional
    public void confirmOrder(Long orderId, Long memberId) {
        Order order = orderAccessGuard.findOwnedOrder(orderId, memberId);
        if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
            return;
        }
        if (!order.getOrderStatus().canTransitTo(OrderStatus.CONFIRMED)) {
            throw new AppException(OrderErrorCode.ORDER_NOT_CONFIRMABLE);
        }
        order.confirmPurchase(OffsetDateTime.now());
    }
}
