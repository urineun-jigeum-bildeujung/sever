package com.golajugaenyang.notification.adapter.out.persistence.mapper;

import com.golajugaenyang.notification.adapter.out.persistence.entity.FcmTokenJpaEntity;
import com.golajugaenyang.notification.domain.entity.FcmToken;

public class FcmTokenMapper {

    public static FcmToken toDomain(FcmTokenJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new FcmToken(jpaEntity.getId(), jpaEntity.getMemberId(), jpaEntity.getToken());
    }
}
