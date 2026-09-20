package com.golajugaenyang.review.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", url = "${member-service.url}", configuration = InternalFeignClientConfig.class)
public interface MemberClient {

    @GetMapping("/internal/v1/members/{memberId}/pets/{petId}")
    void validatePetOwnership(@PathVariable Long memberId, @PathVariable Long petId);
}
