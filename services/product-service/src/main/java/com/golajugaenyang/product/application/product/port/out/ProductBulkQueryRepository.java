package com.golajugaenyang.product.application.product.port.out;

import com.golajugaenyang.product.application.product.port.out.dto.ProductInternalProjection;
import java.util.List;

public interface ProductBulkQueryRepository {

    List<ProductInternalProjection> findAllByIds(List<Long> productIds);
}
