package com.golajugaenyang.member.adapter.in.web.dto.response;

public record SignupResponse(
        String accessToken,
        String refreshToken
) {
}
