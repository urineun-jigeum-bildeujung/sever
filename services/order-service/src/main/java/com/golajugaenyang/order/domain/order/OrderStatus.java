package com.golajugaenyang.order.domain.order;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("결제대기"),
    PAID("결제완료"),
    PREPARING("상품준비"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CONFIRMED("구매확정"),
    CANCELLED("취소"),
    PARTIAL_REFUND("부분환불"),
    REFUNDED("환불완료");

    private final String displayName;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
        PENDING, EnumSet.of(PAID, CANCELLED),
        PAID, EnumSet.of(PREPARING, CANCELLED, REFUNDED),
        PREPARING, EnumSet.of(SHIPPING, CANCELLED, REFUNDED),
        SHIPPING, EnumSet.of(DELIVERED),
        DELIVERED, EnumSet.of(CONFIRMED, PARTIAL_REFUND, REFUNDED),
        CONFIRMED, EnumSet.noneOf(OrderStatus.class),
        CANCELLED, EnumSet.noneOf(OrderStatus.class),
        PARTIAL_REFUND, EnumSet.noneOf(OrderStatus.class),
        REFUNDED, EnumSet.noneOf(OrderStatus.class)
    );

    public boolean canTransitTo(OrderStatus next) {
        return ALLOWED.getOrDefault(this, EnumSet.noneOf(OrderStatus.class)).contains(next);
    }
}
