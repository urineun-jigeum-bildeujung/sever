package com.golajugaenyang.order.adapter.out.external.product.client;

import com.golajugaenyang.order.adapter.out.external.product.dto.ProductInternalItemsResponse;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange
public interface ProductInternalApiClient {

    @GetExchange("/internal/products")
    ProductInternalItemsResponse getProducts(@RequestParam("ids") List<Long> ids);
}
