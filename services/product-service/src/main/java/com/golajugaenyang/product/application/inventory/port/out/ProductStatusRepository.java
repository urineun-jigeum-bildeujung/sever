package com.golajugaenyang.product.application.inventory.port.out;

import com.golajugaenyang.product.domain.product.Product;

public interface ProductStatusRepository {

    Product findById(Long productId);
}
