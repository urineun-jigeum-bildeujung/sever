package com.golajugaenyang.order.application.order.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.out.EventOutboxPort;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderStatus;
import com.golajugaenyang.order.domain.order.OrderTestFixtures;
import com.golajugaenyang.order.error.OrderErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CancelOrderServiceTest {

    @Mock
    private OrderAccessGuard orderAccessGuard;

    @Mock
    private EventOutboxPort eventOutboxPort;

    @InjectMocks
    private CancelOrderService cancelOrderService;

    @Test
    @DisplayName("취소 가능한 주문은 전체 취소되고 품목 수만큼 이벤트가 발행된다.")
    void cancel_order_success_and_publishes_event_per_item() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.PAID, 1L);
        order.addItem(OrderTestFixtures.newOrderItem(101L, 1001L, 2));
        order.addItem(OrderTestFixtures.newOrderItem(102L, 1002L, 1));
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        cancelOrderService.cancelOrder(10L, 1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(eventOutboxPort, times(2)).enqueue(any(), eq("order.item-cancelled"), any());
    }

    @Test
    @DisplayName("배송이 시작된 주문은 취소가 거부되고 이벤트가 발행되지 않는다.")
    void cancel_order_throws_when_not_cancellable() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.SHIPPING, 1L);
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        assertThatThrownBy(() -> cancelOrderService.cancelOrder(10L, 1L))
            .isInstanceOf(AppException.class)
            .hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.ORDER_NOT_CANCELLABLE);

        verifyNoInteractions(eventOutboxPort);
    }

    @Test
    @DisplayName("이미 취소된 주문은 멱등하게 성공 처리되고 이벤트가 재발행되지 않는다")
    void cancel_order_is_idempotent_when_already_cancelled() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.CANCELLED, 1L);
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        cancelOrderService.cancelOrder(10L, 1L);

        verifyNoInteractions(eventOutboxPort);
    }

}
