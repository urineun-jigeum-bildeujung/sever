package com.golajugaenyang.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank String refreshToken
) {
}
