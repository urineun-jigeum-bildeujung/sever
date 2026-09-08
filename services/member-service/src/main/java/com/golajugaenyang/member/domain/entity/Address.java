package com.golajugaenyang.member.domain.entity;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Address {

    private Long id;
    private String addressName;
    private String receiver;
    private String address;
    private String addressDetail;
    private boolean isDefault;
    private String deliveryNode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long memberId;

    public Address(Long id, String addressName, String receiver, String address,
        String addressDetail,
        boolean isDefault, String deliveryNode, LocalDateTime createdAt, LocalDateTime updatedAt,
        Long memberId) {
        this.id = id;
        this.addressName = addressName;
        this.receiver = receiver;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
        this.deliveryNode = deliveryNode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberId = memberId;
    }

}
