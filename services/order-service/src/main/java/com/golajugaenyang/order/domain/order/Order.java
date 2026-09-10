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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "order_type", nullable = false, length = 20)
    private OrderType orderType;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public boolean isClaimableForReturn() {
        return deliveredAt != null
            && orderStatus == OrderStatus.DELIVERED
            && deliveredAt.plusDays(7).isAfter(OffsetDateTime.now());
    }

    public boolean isCancellable() {
        return orderStatus == OrderStatus.PAID || orderStatus == OrderStatus.PREPARING;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignTo(this);
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
