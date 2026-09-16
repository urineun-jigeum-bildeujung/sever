package com.golajugaenyang.member.adapter.in.web.dto.response;

public record AddressDetailResponse(
    Long addressId,
    String addressName,
    String receiver,
    String phone,
    String zipCode,
    String address,
    String addressDetail,
    String deliveryNote,
    boolean isDefault
) {
}
