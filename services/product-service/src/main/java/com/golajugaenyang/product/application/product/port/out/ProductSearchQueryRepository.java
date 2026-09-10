package com.golajugaenyang.product.application.product.port.out;

import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import com.golajugaenyang.product.application.product.port.out.dto.ProductSearchCriteria;
import java.util.List;

public interface ProductSearchQueryRepository {

    List<ProductListProjection> search(ProductSearchCriteria criteria);

    long count(ProductSearchCriteria criteria);
}
