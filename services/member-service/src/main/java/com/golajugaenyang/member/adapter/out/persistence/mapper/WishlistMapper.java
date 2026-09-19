package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.WishlistJpaEntity;
import com.golajugaenyang.member.domain.entity.Wishlist;

public class WishlistMapper {

    public static Wishlist toDomain(WishlistJpaEntity jpaEntity) {
        if(jpaEntity == null) return null;
        return new Wishlist(
                jpaEntity.getId(),
                jpaEntity.getMemberId(),
                jpaEntity.getProductId(),
                jpaEntity.getCreatedAt()
        );
    }

    public static WishlistJpaEntity toJpaEntity(Wishlist domain) {
        if(domain == null) return null;
        return WishlistJpaEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .productId(domain.getProductId())
                .build();
    }
}
