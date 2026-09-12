package com.golajugaenyang.product.application.product.port.out;

import com.golajugaenyang.product.application.product.port.out.dto.ProductDetailProjection;
import java.util.Optional;

public interface ProductDetailQueryRepository {

    Optional<ProductDetailProjection> findDetailById(Long productId);
}
