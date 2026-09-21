package com.golajugaenyang.notification.domain.entity;

import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import lombok.Getter;

@Getter
public class NotificationSubscription {

    private Long id;
    private Long memberId;
    private NotificationCategory category;
    private boolean subscribed;

    public NotificationSubscription(Long id, Long memberId, NotificationCategory category, boolean subscribed) {
        this.id = id;
        this.memberId = memberId;
        this.category = category;
        this.subscribed = subscribed;
    }
}
