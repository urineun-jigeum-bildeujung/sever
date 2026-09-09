package com.golajugaenyang.product.application.product.port.in;

import com.golajugaenyang.product.application.product.port.in.dto.ProductListCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductListResult;


public interface ProductListUseCase {

    ProductListResult getProductList(ProductListCommand command);
}
