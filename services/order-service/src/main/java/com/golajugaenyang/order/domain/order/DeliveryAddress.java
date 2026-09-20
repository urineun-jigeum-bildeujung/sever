package com.golajugaenyang.order.domain.order;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_name", nullable = false, length = 50)
    private String addressName;

    @Column(name = "receiver", nullable = false, length = 50)
    private String receiver;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "address_detail", length = 100)
    private String addressDetail;

    // TODO: member-service 실제 연동 후속 작업 대체
    public static DeliveryAddress placeholder(Long addressId) {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.addressId = addressId;
        deliveryAddress.addressName = "기본 배송지";
        deliveryAddress.receiver = "미확인";
        deliveryAddress.receiverPhone = "000-0000-0000";
        deliveryAddress.address = "(addressId=" + addressId + ")";
        return deliveryAddress;
    }
}
