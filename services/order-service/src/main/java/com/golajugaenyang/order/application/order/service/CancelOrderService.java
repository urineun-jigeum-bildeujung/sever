package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.in.CancelOrderUseCase;
import com.golajugaenyang.order.application.order.port.out.EventOutboxPort;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderStatus;
import com.golajugaenyang.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderAccessGuard orderAccessGuard;
    private final EventOutboxPort eventOutboxPort;

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = orderAccessGuard.findOwnedOrder(orderId, memberId);
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return;
        }
        if (!order.isCancellable()) {
            throw new AppException(OrderErrorCode.ORDER_NOT_CANCELLABLE);
        }

        order.cancelAll();
        order.getItems().forEach(item -> eventOutboxPort.enqueue(
            order.getId(),
            "order.item-cancelled",
            OrderItemReservationMapper.toCancelledPayload(order.getId(), item)));
    }

}
