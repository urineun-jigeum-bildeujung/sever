package com.golajugaenyang.order.adapter.out.external.inventory.client;

import com.golajugaenyang.order.adapter.out.external.inventory.dto.ReserveBulkRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface InventoryInternalApiClient {

    @PostExchange("/internal/inventory/reserve-bulk")
    void reserveBulk(@RequestBody ReserveBulkRequest request);
}
