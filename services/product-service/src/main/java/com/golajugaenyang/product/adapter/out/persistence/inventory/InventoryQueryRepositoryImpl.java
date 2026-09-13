package com.golajugaenyang.product.adapter.out.persistence.inventory;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.product.application.inventory.port.out.InventoryQueryRepository;
import com.golajugaenyang.product.domain.inventory.Inventory;
import com.golajugaenyang.product.error.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryQueryRepositoryImpl implements InventoryQueryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public Inventory findByProductId(Long productId) {
        return inventoryJpaRepository.findById(productId)
            .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }
}
