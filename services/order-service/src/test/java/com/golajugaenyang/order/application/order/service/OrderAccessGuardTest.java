package com.golajugaenyang.order.application.order.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
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
public class OrderAccessGuardTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private OrderAccessGuard orderAccessGuard;

    @Test
    @DisplayName("주문이 존재하고 소유자가 일치하면 주문을 반환한다")
    void find_owned_order_success_when_order_exists_and_owner_matches() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.PAID, 1L);
        when(orderRepositoryPort.findByIdOrNull(10L)).thenReturn(order);

        Order result = orderAccessGuard.findOwnedOrder(10L, 1L);

        assertThat(result).isSameAs(order);
    }

    @Test
    @DisplayName("주문이 존재하지 않으면 ORDER_NOT_FOUND 예외를 던진다.")
    void find_owned_order_throws_when_order_not_found() {
        when(orderRepositoryPort.findByIdOrNull(10L)).thenReturn(null);

        assertThatThrownBy(() -> orderAccessGuard.findOwnedOrder(10L, 1L))
            .isInstanceOf(AppException.class)
            .hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 소유자가 다르면 ORDER_OWNER_MISMATCH 예외를 던진다.")
    void find_owned_order_throws_when_member_mismatch() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.PAID, 1L);
        when(orderRepositoryPort.findByIdOrNull(10L)).thenReturn(order);

        assertThatThrownBy(() -> orderAccessGuard.findOwnedOrder(10L, 999L))
            .isInstanceOf(AppException.class)
            .hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.ORDER_OWNER_MISMATCH);
    }
}
