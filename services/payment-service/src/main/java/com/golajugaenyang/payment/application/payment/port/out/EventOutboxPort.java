package com.golajugaenyang.payment.application.payment.port.out;

public interface EventOutboxPort {

    void enqueue(Long aggregateId, String eventType, Object payload);
}
