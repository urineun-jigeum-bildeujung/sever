package com.golajugaenyang.member.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.member.adapter.out.client.dto.ProductInternalItemsResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${product-service.url}", configuration = InternalFeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/internal/products")
    ProductInternalItemsResponse getProducts(@RequestParam("ids") List<Long> ids);
}
