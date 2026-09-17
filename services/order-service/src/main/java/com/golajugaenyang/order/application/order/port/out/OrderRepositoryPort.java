package com.golajugaenyang.order.application.order.port.out;

import com.golajugaenyang.order.domain.order.Order;

public interface OrderRepositoryPort {

    boolean existsByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    Order findByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    Order findById(Long id);

    Order findByIdOrNull(Long id);

    Order findByOrderItemIdOrNull(Long orderItemId);

    Order save(Order order);

    String generateOrderNumber();
}
