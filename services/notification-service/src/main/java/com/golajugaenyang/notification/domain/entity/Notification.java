package com.golajugaenyang.notification.domain.entity;

import com.golajugaenyang.notification.domain.entity.enums.NotificationType;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Notification {

    private Long id;
    private Long memberId;
    private NotificationType type;
    private String title;
    private String body;
    private String deepLink;
    private boolean read;
    private Instant createdAt;

    public Notification(
            Long id, Long memberId, NotificationType type,
            String title, String body, String deepLink, boolean read, Instant createdAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.deepLink = deepLink;
        this.read = read;
        this.createdAt = createdAt;
    }
}
