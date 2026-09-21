package com.golajugaenyang.notification.domain.repository;

import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import java.util.List;

public interface NotificationSubscriptionRepository {

    void upsert(Long memberId, NotificationCategory category, boolean subscribed);

    boolean isSubscribed(Long memberId, NotificationCategory category);

    List<Long> findSubscribedMemberIds(NotificationCategory category);
}
