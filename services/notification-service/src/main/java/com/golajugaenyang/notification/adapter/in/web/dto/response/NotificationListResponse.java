package com.golajugaenyang.notification.adapter.in.web.dto.response;

import com.golajugaenyang.notification.domain.entity.Notification;
import java.time.Instant;
import java.util.List;

public record NotificationListResponse(
        List<Item> content
) {

    public record Item(
            Long notificationId,
            String type,
            String title,
            String body,
            boolean isRead,
            String targetType,
            String targetId,
            Instant createdAt
    ) {

    }

    public static NotificationListResponse from(List<Notification> notifications) {
        List<Item> content = notifications.stream()
                .map(n -> new Item(
                        n.getId(),
                        n.getType().name(),
                        n.getTitle(),
                        n.getBody(),
                        n.isRead(),
                        n.getTargetType() != null ? n.getTargetType().name() : null,
                        n.getTargetId(),
                        n.getCreatedAt()))
                .toList();
        return new NotificationListResponse(content);
    }
}
