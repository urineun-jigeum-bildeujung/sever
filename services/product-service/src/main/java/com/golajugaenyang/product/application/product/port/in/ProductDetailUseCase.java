package com.golajugaenyang.product.application.product.port.in;

import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;

public interface ProductDetailUseCase {

    ProductDetailResult getProductDetail(Long productId);
}
