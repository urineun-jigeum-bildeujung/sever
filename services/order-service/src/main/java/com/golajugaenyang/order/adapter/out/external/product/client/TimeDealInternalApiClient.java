package com.golajugaenyang.order.adapter.out.external.product.client;

import com.golajugaenyang.order.adapter.out.external.product.dto.TimeDealInternalItemsResponse;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange
public interface TimeDealInternalApiClient {

    @GetExchange("/internal/time-deal-items")
    TimeDealInternalItemsResponse getTimeDealItems(@RequestParam("ids") List<Long> ids);
}
