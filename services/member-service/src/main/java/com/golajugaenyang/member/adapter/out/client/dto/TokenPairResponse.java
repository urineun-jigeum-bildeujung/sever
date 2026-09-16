package com.golajugaenyang.member.adapter.out.client.dto;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {
}
