package com.golajugaenyang.payment.domain.payment;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_payments_order_id", columnNames = "order_id"),
        @UniqueConstraint(name = "uq_payments_payment_key", columnNames = "payment_key")
    },
    indexes = @Index(name = "idx_payments_member", columnList = "member_id, requested_at DESC"))
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq")
    @SequenceGenerator(name = "payments_seq", sequenceName = "payments_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_number", nullable = false, length = 64)
    private String orderNumber;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.READY;

    @Column(name = "method", length = 30)
    private String method;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    public static Payment requestFor(
        Long orderId, String orderNumber, Long memberId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.orderNumber = orderNumber;
        payment.memberId = memberId;
        payment.amount = amount;
        payment.paymentStatus = PaymentStatus.READY;
        payment.requestedAt = OffsetDateTime.now();
        return payment;
    }

    public void confirmSuccess(
        String paymentKey, String method, OffsetDateTime approvedAt) {
        if (!this.paymentStatus.canTransitTo(PaymentStatus.DONE)) {
            throw new IllegalStateException(
                "id=%d, 현재 상태 %s에서 DONE으로 전이할 수 없습니다.".formatted(getId(), this.paymentStatus));
        }
        this.paymentKey = paymentKey;
        this.method = method;
        this.approvedAt = approvedAt;
        this.paymentStatus = PaymentStatus.DONE;
    }

    public void confirmFailure(String paymentKey, String reason) {
        if (!this.paymentStatus.canTransitTo(PaymentStatus.FAILED)) {
            throw new IllegalStateException(
                "id=%d, 현재 상태 %s에서 FAILED로 전이할 수 없습니다.".formatted(getId(), this.paymentStatus));
        }
        this.paymentKey = paymentKey;
        this.failReason = reason;
        this.paymentStatus = PaymentStatus.FAILED;
    }
}
