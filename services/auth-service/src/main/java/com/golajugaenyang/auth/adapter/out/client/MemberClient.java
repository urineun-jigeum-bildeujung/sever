package com.golajugaenyang.auth.adapter.out.client;

import com.golajugaenyang.auth.adapter.out.client.dto.NicknameSuggestionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "member-service", url = "${member-service.url}")
public interface MemberClient {

    @GetMapping("/internal/users/nickname")
    NicknameSuggestionResponse getNicknameSuggestion();
}
