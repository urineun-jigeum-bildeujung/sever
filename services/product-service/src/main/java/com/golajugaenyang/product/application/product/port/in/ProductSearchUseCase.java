package com.golajugaenyang.product.application.product.port.in;

import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchCommand;
import com.golajugaenyang.product.application.product.port.in.dto.ProductSearchResult;


public interface ProductSearchUseCase {

    ProductSearchResult searchProducts(ProductSearchCommand command);
}
