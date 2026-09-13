package com.golajugaenyang.product.application.inventory.port.out;

import com.golajugaenyang.product.domain.inventory.StockMovement;
import com.golajugaenyang.product.domain.inventory.StockMovementType;
import java.util.Optional;

public interface StockMovementRepository {

    boolean recordIfAbsent(StockMovement movement);

    Optional<Integer> findQuantity(Long orderItemId, StockMovementType movementType);
}
