package com.golajugaenyang.member.adapter.in.web.dto.request;

import com.golajugaenyang.member.domain.entity.enums.Carrier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PhoneRegisterRequest(
        @NotBlank String phone,
        @NotNull Carrier carrier,
        int code
) {
}
