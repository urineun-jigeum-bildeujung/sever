package com.golajugaenyang.member.domain.entity;

import java.time.Instant;

import lombok.Getter;

@Getter
public class Address {

    private Long id;
    private String addressName;
    private String receiver;
    private String receiverPhone;
    private String zipCode;
    private String address;
    private String addressDetail;
    private boolean isDefault;
    private String deliveryNote;
    private Instant createdAt;
    private Instant updatedAt;
    private Long memberId;

    public Address(Long id, String addressName, String receiver, String receiverPhone,
        String zipCode, String address, String addressDetail,
        boolean isDefault, String deliveryNote, Instant createdAt, Instant updatedAt,
        Long memberId) {
        this.id = id;
        this.addressName = addressName;
        this.receiver = receiver;
        this.receiverPhone = receiverPhone;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
        this.deliveryNote = deliveryNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberId = memberId;
    }

}
