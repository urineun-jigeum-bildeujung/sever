package com.golajugaenyang.member.adapter.in.web.dto.response;

public record AddressSnapshotResponse(
        String addressName,
        String receiver,
        String receiverPhone,
        String zipCode,
        String address,
        String addressDetail,
        String deliveryNote
) {
}
