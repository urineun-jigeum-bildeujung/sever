package com.golajugaenyang.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotNull;

public record MemberMyEmailRequest(
        @NotNull Long authId
) {
}
