package com.golajugaenyang.product.application.inventory;


import com.golajugaenyang.product.application.inventory.port.out.InventoryQueryRepository;
import com.golajugaenyang.product.application.inventory.port.out.ProductStatusRepository;
import com.golajugaenyang.product.domain.inventory.Inventory;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import com.golajugaenyang.product.domain.product.Product;
import com.golajugaenyang.product.domain.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductStatusSyncListener {

    private final InventoryQueryRepository inventoryQueryRepository;
    private final ProductStatusRepository productStatusRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(StockChangedEvent event) {
        if (event.subjectType() != StockSubjectType.PRODUCT) {
            return;
        }

        Inventory inventory = inventoryQueryRepository.findByProductId(event.subjectId());
        Product product = productStatusRepository.findById(event.subjectId());

        if (inventory.availableStock() <= 0
            && product.getStatus() == ProductStatus.ON_SALE) {
            product.markSoldOut();
        } else if (inventory.availableStock() > 0
            && product.getStatus() == ProductStatus.SOLD_OUT) {
            product.markOnSale();
        }
    }
}
