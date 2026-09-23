package com.golajugaenyang.member.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.member.adapter.out.client.dto.ReviewRatingsInternalResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "review-service", url = "${review-service.url}", configuration = InternalFeignClientConfig.class)
public interface ReviewClient {

    @GetMapping("/internal/reviews/products/ratings")
    ReviewRatingsInternalResponse getProductRatings(@RequestParam("productIds") List<Long> productIds);
}
