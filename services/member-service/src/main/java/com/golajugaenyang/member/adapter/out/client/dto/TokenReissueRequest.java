package com.golajugaenyang.member.adapter.out.client.dto;

public record TokenReissueRequest(
        Long authId,
        Long memberId
) {
}
