package com.golajugaenyang.product.adapter.out.persistence.inventory;

import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface StockMovementJpaRepository extends JpaRepository<StockMovement, Long> {

    Optional<StockMovement> findByOrderItemIdAndMovementType(
        Long orderItemId, StockMovementType movementType);
}
