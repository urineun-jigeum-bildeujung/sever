package com.golajugaenyang.auth.adapter.out.persistence.mapper;

import com.golajugaenyang.auth.adapter.out.persistence.entity.AuthJpaEntity;
import com.golajugaenyang.auth.domain.entity.Auth;

public class AuthMapper {

    public static Auth toDomain(AuthJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new Auth(
            jpaEntity.getId(),
            jpaEntity.getProvider(),
            jpaEntity.getSocialId(),
            jpaEntity.getSocialEmail(),
            jpaEntity.getStatus(),
            jpaEntity.getCreatedAt() == null ? null : jpaEntity.getCreatedAt().toLocalDateTime(),
            jpaEntity.getUpdatedAt() == null ? null : jpaEntity.getUpdatedAt().toLocalDateTime()
        );
    }

    public static AuthJpaEntity toJpaEntity(Auth domain){
        if(domain == null) return null;
        return AuthJpaEntity.builder()
            .id(domain.getId())
            .provider(domain.getProvider())
            .socialId(domain.getSocialId())
            .socialEmail(domain.getSocialEmail())
            .status(domain.getStatus())
            .build();
    }
}
