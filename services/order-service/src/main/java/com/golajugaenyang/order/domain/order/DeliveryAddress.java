package com.golajugaenyang.order.domain.order;


import com.golajugaenyang.order.application.order.port.out.dto.AddressInfo;
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

    public static DeliveryAddress from(Long addressId, AddressInfo info) {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.addressId = addressId;
        deliveryAddress.addressName = info.addressName();
        deliveryAddress.receiver = info.receiver();
        deliveryAddress.receiverPhone = info.receiverPhone();
        deliveryAddress.zipCode = info.zipCode();
        deliveryAddress.address = info.address();
        deliveryAddress.addressDetail = info.addressDetail();
        return deliveryAddress;
    }
    
}
