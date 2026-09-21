package com.golajugaenyang.notification.domain.repository;

import com.golajugaenyang.notification.domain.entity.Notification;
import java.util.List;

public interface NotificationRepository {

    void saveAll(List<Notification> notifications);

    List<Notification> findByMemberId(Long memberId, int page, int size);

    int markAsRead(Long memberId, Long notificationId);
}
