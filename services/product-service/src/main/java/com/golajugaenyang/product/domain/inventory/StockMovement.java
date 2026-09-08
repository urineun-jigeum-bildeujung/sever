package com.golajugaenyang.product.domain.inventory;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "stock_movements",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_stock_movement",
        columnNames = {"order_item_id", "movement_type"}),
    indexes = @Index(name = "idx_stock_movements_product", columnList = "product_id"))
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private StockMovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
