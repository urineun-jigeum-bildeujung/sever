package com.golajugaenyang.notification.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.notification.adapter.out.client.dto.TimeDealListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "product-service",
    url = "${product-service.url}",
    configuration = InternalFeignClientConfig.class
)
public interface ProductClient {

    @GetMapping("/api/v1/time-deals")
    TimeDealListResponse getTimeDeals(@RequestParam("status") String status);
}
