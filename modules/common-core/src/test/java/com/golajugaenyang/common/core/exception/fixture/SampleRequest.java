package com.golajugaenyang.common.core.exception.fixture;

import jakarta.validation.constraints.NotBlank;

public record SampleRequest(
    @NotBlank String name
) {
}
