package com.golajugaenyang.product.application.inventory;


import com.golajugaenyang.product.application.inventory.port.out.InventoryQueryRepository;
import com.golajugaenyang.product.application.inventory.port.out.ProductStatusRepository;
import com.golajugaenyang.product.domain.inventory.Inventory;
import com.golajugaenyang.product.domain.product.Product;
import com.golajugaenyang.product.domain.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductStatusSyncer {

    private final InventoryQueryRepository inventoryQueryRepository;
    private final ProductStatusRepository productStatusRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sync(Long productId) {
        Inventory inventory = inventoryQueryRepository.findByProductId(productId);
        Product product = productStatusRepository.findById(productId);

        if (inventory.availableStock() <= 0
            && product.getStatus() == ProductStatus.ON_SALE) {
            product.markSoldOut();
        } else if (inventory.availableStock() > 0
            && product.getStatus() == ProductStatus.SOLD_OUT) {
            product.markOnSale();
        }
    }
}
