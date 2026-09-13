package com.golajugaenyang.product.application.inventory.port.out;


public interface InventoryCommandRepository {

    int reserve(Long productId, int quantity);

    int confirm(Long productId, int quantity);

    int release(Long productId, int quantity);

    int restore(Long productId, int quantity);
}
