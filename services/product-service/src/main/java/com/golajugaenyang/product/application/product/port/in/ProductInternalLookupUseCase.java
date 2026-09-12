package com.golajugaenyang.product.application.product.port.in;

import com.golajugaenyang.product.application.product.port.in.dto.ProductInternalLookupResult;
import java.util.List;

public interface ProductInternalLookupUseCase {

    ProductInternalLookupResult getProducts(List<Long> productIds);
}
