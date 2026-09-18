package com.golajugaenyang.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotNull;

public record TokenInternalReissueRequest(
        @NotNull Long authId,
        @NotNull Long memberId
) {

}
