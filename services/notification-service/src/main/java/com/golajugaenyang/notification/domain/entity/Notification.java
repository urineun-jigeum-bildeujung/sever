package com.golajugaenyang.notification.domain.entity;

import com.golajugaenyang.notification.domain.entity.enums.NotificationDisplayType;
import com.golajugaenyang.notification.domain.entity.enums.NotificationTargetType;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Notification {

    private Long id;
    private Long memberId;
    private NotificationDisplayType type;
    private String title;
    private String body;
    private NotificationTargetType targetType;
    private String targetId;
    private boolean read;
    private Instant createdAt;

    public Notification(
            Long id, Long memberId, NotificationDisplayType type,
            String title, String body,
            NotificationTargetType targetType, String targetId,
            boolean read, Instant createdAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.targetType = targetType;
        this.targetId = targetId;
        this.read = read;
        this.createdAt = createdAt;
    }
}
