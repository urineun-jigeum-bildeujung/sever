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
            jpaEntity.getAddress(),
            jpaEntity.getAddressDetail(),
            jpaEntity.isDefault(),
            jpaEntity.getDeliveryNode(),
            jpaEntity.getCreatedAt() == null ? null : jpaEntity.getCreatedAt().toLocalDateTime(),
            jpaEntity.getUpdatedAt() == null ? null : jpaEntity.getUpdatedAt().toLocalDateTime(),
            jpaEntity.getMemberId()
        );
    }

    public static AddressJpaEntity toJpaEntity(Address domain){
        if(domain == null) return null;
        return AddressJpaEntity.builder()
            .id(domain.getId())
            .addressName(domain.getAddressName())
            .receiver(domain.getReceiver())
            .address(domain.getAddress())
            .addressDetail(domain.getAddressDetail())
            .isDefault(domain.isDefault())
            .deliveryNode(domain.getDeliveryNode())
            .memberId(domain.getMemberId())
            .build();
    }

}
