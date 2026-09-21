package com.golajugaenyang.order.application.order.port.out.dto;

public record AddressInfo(
    String addressName,
    String receiver,
    String receiverPhone,
    String zipCode,
    String address,
    String addressDetail,
    String deliveryNote
) {

}
