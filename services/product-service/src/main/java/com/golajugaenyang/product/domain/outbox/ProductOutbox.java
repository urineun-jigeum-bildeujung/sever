package com.golajugaenyang.product.domain.outbox;


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
    name = "product_outbox",
    indexes = @Index(name = "idx_product_outbox_status", columnList = "status, created_at"))
public class ProductOutbox extends BaseOutboxEntity {

    private ProductOutbox(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        super(aggregateType, aggregateId, eventType, payload);
    }

    public static ProductOutbox create(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        return new ProductOutbox(aggregateType, aggregateId, eventType, payload);
    }
}
