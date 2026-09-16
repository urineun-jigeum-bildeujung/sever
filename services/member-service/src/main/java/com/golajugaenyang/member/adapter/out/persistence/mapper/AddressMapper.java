package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.AddressJpaEntity;
import com.golajugaenyang.member.domain.entity.Address;

public class AddressMapper {

    public static Address toDomain(AddressJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new Address(
            jpaEntity.getId(),
            jpaEntity.getAddressName(),
            jpaEntity.getReceiver(),
            jpaEntity.getReceiverPhone(),
            jpaEntity.getZipCode(),
            jpaEntity.getAddress(),
            jpaEntity.getAddressDetail(),
            jpaEntity.isDefault(),
            jpaEntity.getDeliveryNote(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt(),
            jpaEntity.getMemberId()
        );
    }

    public static AddressJpaEntity toJpaEntity(Address domain){
        if(domain == null) return null;
        return AddressJpaEntity.builder()
            .id(domain.getId())
            .addressName(domain.getAddressName())
            .receiver(domain.getReceiver())
            .receiverPhone(domain.getReceiverPhone())
            .zipCode(domain.getZipCode())
            .address(domain.getAddress())
            .addressDetail(domain.getAddressDetail())
            .isDefault(domain.isDefault())
            .deliveryNote(domain.getDeliveryNote())
            .memberId(domain.getMemberId())
            .build();
    }

}
