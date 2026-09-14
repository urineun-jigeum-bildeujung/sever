package com.golajugaenyang.product.application.inventory;


import com.golajugaenyang.product.application.inventory.port.out.ProductStatusReconciliationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStatusReconciliationJob {

    private final ProductStatusReconciliationRepository reconciliationRepository;
    private final ProductStatusSyncer productStatusSyncer;

    @Scheduled(fixedDelay = 300_000)
    public void reconcile() {
        List<Long> mismatchedProductIds =
            reconciliationRepository.findStatusMismatchedProductIds();
        if (mismatchedProductIds.isEmpty()) {
            return;
        }
        log.warn("[ProductStatusReconciliation] {}개 상품의 상태 불일치 수정: {}",
            mismatchedProductIds.size(), mismatchedProductIds);
        mismatchedProductIds.forEach(productStatusSyncer::sync);
    }
}
