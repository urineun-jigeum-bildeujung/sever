package com.golajugaenyang.order.application.order.port.out;

public interface EventOutboxPort {

    void enqueue(Long aggregateId, String eventType, Object payload);
}
