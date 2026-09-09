package com.golajugaenyang.product.application.product.port.out;

import com.golajugaenyang.product.application.product.port.out.dto.ProductListCriteria;
import com.golajugaenyang.product.application.product.port.out.dto.ProductListProjection;
import java.util.List;


public interface ProductQueryRepository {

    List<ProductListProjection> findProductList(ProductListCriteria criteria);
}
