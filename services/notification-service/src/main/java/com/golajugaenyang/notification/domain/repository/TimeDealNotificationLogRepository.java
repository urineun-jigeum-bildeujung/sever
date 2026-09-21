package com.golajugaenyang.notification.domain.repository;

import com.golajugaenyang.notification.domain.entity.enums.NotificationType;

public interface TimeDealNotificationLogRepository {

    /**
     * @return 새로 기록되어 알림을 보내야 하면 true, 이미 보낸 적이 있으면 false
     */
    boolean tryMarkAsSent(Long dealId, NotificationType notificationType);
}
