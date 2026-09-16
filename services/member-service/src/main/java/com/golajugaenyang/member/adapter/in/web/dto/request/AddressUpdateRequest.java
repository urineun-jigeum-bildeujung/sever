package com.golajugaenyang.member.adapter.in.web.dto.request;

import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
        String addressName,
        String receiver,
        String phone,
        String zipCode,
        String address,
        String addressDetail,
        @Size(max = 100) String deliveryNote,
        Boolean isDefault
) {
}
