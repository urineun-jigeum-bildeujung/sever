package com.golajugaenyang.order.domain.outbox;

import com.golajugaenyang.common.jpa.entity.BaseOutboxEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "order_outbox",
    indexes = @Index(name = "idx_order_outbox_status", columnList = "status, created_at"))
public class OrderOutbox extends BaseOutboxEntity {

    private OrderOutbox(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        super(aggregateType, aggregateId, eventType, payload);
    }

    public static OrderOutbox create(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        return new OrderOutbox(aggregateType, aggregateId, eventType, payload);
    }
}
