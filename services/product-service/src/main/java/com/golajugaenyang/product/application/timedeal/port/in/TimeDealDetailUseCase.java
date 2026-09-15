package com.golajugaenyang.product.application.timedeal.port.in;

import com.golajugaenyang.product.application.product.port.in.dto.ProductDetailResult;

public interface TimeDealDetailUseCase {

    ProductDetailResult getTimeDealDetail(Long timeDealItemId);
}
