package com.golajugaenyang.review.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.review.adapter.out.client.dto.NicknameInternalItemsResponse;
import com.golajugaenyang.review.adapter.out.client.dto.PetSnapshotResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service", url = "${member-service.url}", configuration = InternalFeignClientConfig.class)
public interface MemberClient {

    @GetMapping("/internal/v1/members/{memberId}/pets/{petId}")
    PetSnapshotResponse getPetSnapshot(@PathVariable Long memberId, @PathVariable Long petId);

    @GetMapping("/internal/v1/members/nicknames")
    NicknameInternalItemsResponse getNicknames(@RequestParam("ids") List<Long> memberIds);
}
