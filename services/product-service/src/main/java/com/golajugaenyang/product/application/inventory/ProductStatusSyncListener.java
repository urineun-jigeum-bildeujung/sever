package com.golajugaenyang.product.application.inventory;


import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class ProductStatusSyncListener {

    private final ProductStatusSyncer productStatusSyncer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockChangedEvent event) {
        if (event.subjectType() != StockSubjectType.PRODUCT) {
            return;
        }
        productStatusSyncer.sync(event.subjectId());
    }
}
