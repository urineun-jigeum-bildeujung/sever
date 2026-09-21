package com.golajugaenyang.order.adapter.out.external.member.dto;

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
