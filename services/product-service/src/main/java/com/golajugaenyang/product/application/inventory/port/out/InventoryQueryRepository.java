package com.golajugaenyang.product.application.inventory.port.out;

import com.golajugaenyang.product.domain.inventory.Inventory;

public interface InventoryQueryRepository {

    Inventory findByProductId(Long productId);
}
