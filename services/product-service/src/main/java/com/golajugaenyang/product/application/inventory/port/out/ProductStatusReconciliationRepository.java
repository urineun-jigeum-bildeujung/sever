package com.golajugaenyang.product.application.inventory.port.out;

import java.util.List;

public interface ProductStatusReconciliationRepository {

    List<Long> findStatusMismatchedProductIds();
}
