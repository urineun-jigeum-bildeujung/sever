package com.golajugaenyang.order.domain.order;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "orders",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_orders_number", columnNames = "order_number"),
        @UniqueConstraint(name = "uq_orders_idempotency", columnNames = "idempotency_key")
    },
    indexes = {
        @Index(name = "idx_orders_member", columnList = "member_id, ordered_at DESC"),
        @Index(name = "idx_orders_status_batch", columnList = "order_status, paid_at")
    })
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 30)
    private String orderNumber;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_type", nullable = false, length = 20)
    private PurchaseType purchaseType;

    @Column(name = "ordered_at", nullable = false)
    private OffsetDateTime orderedAt = OffsetDateTime.now();

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "product_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal productAmount;

    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(name = "delivery_note", length = 200)
    private String deliveryNote;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "reservation_expires_at")
    private OffsetDateTime reservationExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false, length = 20)
    private ReservationStatus reservationStatus = ReservationStatus.REQUESTED;

    @Column(name = "reservation_resolved_at")
    private OffsetDateTime reservationResolvedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public static Order createPending(
        String orderNumber,
        String idempotencyKey,
        Long memberId,
        DeliveryAddress deliveryAddress,
        String deliveryNote,
        Duration reservationTtl
    ) {
        Order order = new Order();
        order.orderNumber = orderNumber;
        order.idempotencyKey = idempotencyKey;
        order.memberId = memberId;
        order.purchaseType = PurchaseType.ONE_TIME;
        order.deliveryAddress = deliveryAddress;
        order.deliveryNote = deliveryNote;
        order.orderStatus = OrderStatus.PENDING;
        order.orderedAt = OffsetDateTime.now();
        order.reservationExpiresAt = order.orderedAt.plus(reservationTtl);
        order.productAmount = BigDecimal.ZERO;
        order.shippingFee = BigDecimal.ZERO;
        order.totalAmount = BigDecimal.ZERO;
        return order;
    }

    public void applyAmounts(BigDecimal productAmount, BigDecimal shippingFee) {
        this.productAmount = productAmount.setScale(0, RoundingMode.HALF_UP);
        this.shippingFee = shippingFee.setScale(0, RoundingMode.HALF_UP);
        this.totalAmount = this.productAmount.add(this.shippingFee);
    }

    public boolean isClaimableForReturn() {
        return deliveredAt != null
            && orderStatus == OrderStatus.DELIVERED
            && deliveredAt.plusDays(7).isAfter(OffsetDateTime.now());
    }

    public boolean isCancellable() {
        return orderStatus.canTransitTo(OrderStatus.CANCELLED);
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignTo(this);
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void confirmReservation() {
        if (!this.reservationStatus.canTransitTo(ReservationStatus.CONFIRMED)) {
            throw new IllegalStateException(
                "id=%d, 현재 예약 상태 %s에서 CONFIRMED로 전이할 수 없습니다."
                    .formatted(getId(), this.reservationStatus));
        }
        this.reservationStatus = ReservationStatus.CONFIRMED;
        this.reservationResolvedAt = OffsetDateTime.now();
    }

    public void failReservation() {
        if (!this.reservationStatus.canTransitTo(ReservationStatus.FAILED)) {
            throw new IllegalStateException(
                "id=%d, 현재 예약 상태 %s에서 FAILED로 전이할 수 없습니다."
                    .formatted(getId(), this.reservationStatus));
        }
        this.reservationStatus = ReservationStatus.FAILED;
        this.reservationResolvedAt = OffsetDateTime.now();
        if (this.orderStatus.canTransitTo(OrderStatus.CANCELLED)) {
            this.orderStatus = OrderStatus.CANCELLED;
        }
    }
}
