package com.golajugaenyang.member.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRegisterRequest(
        @NotBlank String addressName,
        @NotBlank String receiver,
        @NotBlank String phone,
        @NotBlank String zipCode,
        @NotBlank String address,
        @NotBlank String addressDetail,
        @Size(max = 100) String deliveryNote,
        boolean isDefault
) {
}
