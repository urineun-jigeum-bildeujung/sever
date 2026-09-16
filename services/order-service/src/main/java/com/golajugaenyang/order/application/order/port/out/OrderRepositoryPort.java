package com.golajugaenyang.order.application.order.port.out;

import com.golajugaenyang.order.domain.order.Order;

public interface OrderRepositoryPort {

    boolean existsByIdempotencyKey(String idempotencyKey);

    Order findByIdempotencyKey(String idempotencyKey);

    Order findById(Long id);

    Order save(Order order);

    String generateOrderNumber();
}
