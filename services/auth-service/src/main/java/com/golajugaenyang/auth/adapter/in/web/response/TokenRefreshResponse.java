package com.golajugaenyang.auth.adapter.in.web.response;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
