package com.golajugaenyang.order.domain.order;

import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderTestFixtures {

    public static Order newOrder(OrderStatus status, Long memberId) {
        Order order = Order.createPending(
            "ORD-TEST", "idem-key",
            memberId, null, null,
            Duration.ofMinutes(15));
        ReflectionTestUtils.setField(order, "orderStatus", status);
        return order;
    }

    public static Order newDeliveredOrder(Long memberId, OffsetDateTime deliveredAt) {
        Order order = newOrder(OrderStatus.DELIVERED, memberId);
        ReflectionTestUtils.setField(order, "deliveredAt", deliveredAt);
        return order;
    }

    public static OrderItem newOrderItem(Long id, Long productId, int quantity) {
        OrderItem item = new OrderItem(); // 같은 패키지라 protected 생성자에 직접 접근 가능
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "productId", productId);
        ReflectionTestUtils.setField(item, "quantity", quantity);
        ReflectionTestUtils.setField(item, "itemStatus", OrderItemStatus.PAID);
        return item;
    }
}
