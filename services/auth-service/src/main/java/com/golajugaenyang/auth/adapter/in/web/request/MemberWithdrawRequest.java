package com.golajugaenyang.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberWithdrawRequest(
        @NotNull Long authId,
        @NotBlank String accessToken
) {
}
