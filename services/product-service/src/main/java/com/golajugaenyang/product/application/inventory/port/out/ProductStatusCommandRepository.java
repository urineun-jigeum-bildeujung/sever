package com.golajugaenyang.product.application.inventory.port.out;

public interface ProductStatusCommandRepository {

    int markSoldOutIfStillOutOfStock(Long productId);

    int markOnSaleIfStillInStock(Long productId);
}
