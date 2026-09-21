package com.golajugaenyang.notification.adapter.out.persistence.mapper;

import com.golajugaenyang.notification.adapter.out.persistence.entity.NotificationJpaEntity;
import com.golajugaenyang.notification.domain.entity.Notification;

public class NotificationMapper {

    public static Notification toDomain(NotificationJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new Notification(
                jpaEntity.getId(),
                jpaEntity.getMemberId(),
                jpaEntity.getType(),
                jpaEntity.getTitle(),
                jpaEntity.getBody(),
                jpaEntity.getTargetType(),
                jpaEntity.getTargetId(),
                jpaEntity.isRead(),
                jpaEntity.getCreatedAt()
        );
    }

    public static NotificationJpaEntity toJpaEntity(Notification domain) {
        if (domain == null) return null;
        return NotificationJpaEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .type(domain.getType())
                .title(domain.getTitle())
                .body(domain.getBody())
                .targetType(domain.getTargetType())
                .targetId(domain.getTargetId())
                .read(domain.isRead())
                .build();
    }
}
