package com.golajugaenyang.product.adapter.out.persistence.inventory;

import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface StockMovementJpaRepository extends JpaRepository<StockMovement, Long> {

    Optional<StockMovement> findByOrderItemIdAndMovementType(
        Long orderItemId, StockMovementType movementType);

    @Modifying
    @Query(value = """
        INSERT INTO stock_movements
            (subject_type, subject_id, order_item_id, movement_type, quantity, created_at)
        VALUES (:subjectType, :subjectId, :orderItemId, :movementType, :quantity, now())
        ON CONFLICT (order_item_id, movement_type) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(
        @Param("subjectType") String subjectType,
        @Param("subjectId") Long subjectId,
        @Param("orderItemId") Long orderItemId,
        @Param("movementType") String movementType,
        @Param("quantity") int quantity
    );
}
