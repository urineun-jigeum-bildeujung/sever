package com.golajugaenyang.member.adapter.out.client.dto;

public record MemberWithdrawRequest(
        Long authId,
        String accessToken
) {
}
