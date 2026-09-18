package com.golajugaenyang.payment.domain.payment;


import com.golajugaenyang.common.jpa.entity.BaseOutboxEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "payment_outbox",
    indexes = @Index(name = "idx_payment_outbox_status", columnList = "status, created_at"))
public class PaymentOutbox extends BaseOutboxEntity {

    @Column(name = "claimed_at")
    private OffsetDateTime claimedAt;

    private PaymentOutbox(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        super(aggregateType, aggregateId, eventType, payload);
    }

    public static PaymentOutbox create(
        String aggregateType, Long aggregateId, String eventType, JsonNode payload) {
        return new PaymentOutbox(aggregateType, aggregateId, eventType, payload);
    }
}
