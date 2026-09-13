package com.golajugaenyang.product.application.inventory.port.out;



public interface TimeDealStockCommandRepository {

    int reserve(Long timeDealItemId, int quantity);

    int confirm(Long timeDealItemId, int quantity);

    int release(Long timeDealItemId, int quantity);

    int restore(Long timeDealItemId, int quantity);
}
