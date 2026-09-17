package com.golajugaenyang.order.domain.order;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_name", nullable = false, length = 50)
    private String addressName;

    @Column(name = "receiver", nullable = false, length = 50)
    private String receiver;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "address_detail", length = 100)
    private String addressDetail;

    public static DeliveryAddress placeholder(Long addressId) {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.addressId = addressId;
        deliveryAddress.addressName = "임시 배송지";
        deliveryAddress.receiver = "미확인";
        deliveryAddress.address = "member-service 연동 전 임시 값 (addressId=" + addressId + ")";
        return deliveryAddress;
    }
}
