package com.golajugaenyang.notification.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.notification.domain.entity.Notification;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import com.golajugaenyang.notification.domain.repository.NotificationRepository;
import com.golajugaenyang.notification.domain.repository.NotificationSubscriptionRepository;
import com.golajugaenyang.notification.error.NotificationErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;

    public void registerFcmToken(Long memberId, String token) {
        fcmTokenRepository.upsert(memberId, token);
    }

    public void updateSubscription(Long memberId, NotificationCategory category, boolean subscribed) {
        subscriptionRepository.upsert(memberId, category, subscribed);
    }

    public boolean getSubscription(Long memberId, NotificationCategory category) {
        return subscriptionRepository.isSubscribed(memberId, category);
    }

    public List<Notification> getMyNotifications(Long memberId, int page, int size) {
        return notificationRepository.findByMemberId(memberId, page, size);
    }

    public void markAsRead(Long memberId, Long notificationId) {
        int updated = notificationRepository.markAsRead(memberId, notificationId);
        if (updated == 0) {
            throw new AppException(NotificationErrorCode.NOT_FOUND);
        }
    }
}
