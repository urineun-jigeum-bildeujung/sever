package com.golajugaenyang.order.application.order.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderStatus;
import com.golajugaenyang.order.domain.order.OrderTestFixtures;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ConfirmOrderServiceTest {

    @Mock
    private OrderAccessGuard orderAccessGuard;

    @InjectMocks
    private ConfirmOrderService confirmOrderService;

    @Test
    @DisplayName("배송완료 상태의 주문은 구매 확정에 성공한다.")
    void confirm_order_success_when_order_is_delivered() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.DELIVERED, 1L);
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        confirmOrderService.confirmOrder(10L, 1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getConfirmedAt()).isNotNull();
    }

    @Test
    @DisplayName("배송완료 상태가 아니면 구매 확정이 거부된다.")
    void confirm_order_throws_when_order_not_deliverable() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.PAID, 1L);
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        assertThatThrownBy(() -> confirmOrderService.confirmOrder(10L, 1L))
            .isInstanceOf(AppException.class)
            .hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.ORDER_NOT_CONFIRMABLE);
    }

    @Test
    @DisplayName("이미 구매 확정된 주문은 멱등하게 성공 처리되고 상태 변경 로직이 다시 실행되지 않는다.")
    void confirm_order_is_idempotent_when_already_confirmed() {
        Order order = OrderTestFixtures.newOrder(OrderStatus.CONFIRMED, 1L);
        OffsetDateTime originalConfirmedAt = OffsetDateTime.now().minusDays(1);
        ReflectionTestUtils.setField(order, "confirmedAt", originalConfirmedAt);
        when(orderAccessGuard.findOwnedOrder(10L, 1L)).thenReturn(order);

        confirmOrderService.confirmOrder(10L, 1L);

        assertThat(order.getConfirmedAt()).isEqualTo(originalConfirmedAt);
    }
}
