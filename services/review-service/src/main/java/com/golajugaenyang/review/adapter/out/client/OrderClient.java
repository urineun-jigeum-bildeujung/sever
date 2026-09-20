package com.golajugaenyang.review.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.review.adapter.out.client.dto.PurchaseVerificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "${order-service.url}", configuration = InternalFeignClientConfig.class)
public interface OrderClient {

    @GetMapping("/internal/orders/purchase-verification")
    PurchaseVerificationResponse getPurchaseVerification(
            @RequestParam Long memberId,
            @RequestParam Long productId
    );
}
