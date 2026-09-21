package com.golajugaenyang.order.adapter.out.external.member.client;

import com.golajugaenyang.order.adapter.out.external.member.dto.AddressSnapshotResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface MemberInternalApiClient {

    @GetExchange("/internal/v1/members/{memberId}/addresses/{addressId}")
    ResponseEntity<AddressSnapshotResponse> getAddress(
        @PathVariable("memberId") Long memberId,
        @PathVariable("addressId") Long addressId);
}
