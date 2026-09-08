package com.golajugaenyang.common.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import lombok.Getter;


@Getter
@MappedSuperclass
public class BaseOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false, length = 30)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = OffsetDateTime.now();
    }

    public void markFailed() {
        if (!this.status.canTransitTo(OutboxStatus.FAILED)) {
            throw new IllegalStateException(
                "id=%d, 현재 상태 %s에서 FAILED로 전이할 수 없습니다.".formatted(getId(), this.status));
        }
        this.status = OutboxStatus.FAILED;
    }
}
