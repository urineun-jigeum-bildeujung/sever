package com.golajugaenyang.notification.application;

import com.golajugaenyang.notification.domain.entity.Notification;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import com.golajugaenyang.notification.domain.entity.enums.NotificationDisplayType;
import com.golajugaenyang.notification.domain.entity.enums.NotificationTargetType;
import com.golajugaenyang.notification.domain.entity.enums.TimeDealTrigger;
import com.golajugaenyang.notification.domain.repository.NotificationRepository;
import com.golajugaenyang.notification.domain.repository.NotificationSubscriptionRepository;
import com.golajugaenyang.notification.domain.repository.TimeDealNotificationLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 발송 로그 기록 + 구독자 조회 + 알림 저장을 하나의 트랜잭션으로 묶는다.
 * 스케줄러에서 이 메서드를 직접(프록시를 거쳐) 호출해야 하므로 별도 빈으로 분리했다
 * (같은 클래스 안에서 호출하면 자기 자신 호출이라 @Transactional 프록시가 적용되지 않음).
 * FCM 발송(외부 네트워크 호출)은 커밋 이후 스케줄러 쪽에서 수행한다.
 */
@Service
@RequiredArgsConstructor
public class TimeDealNotificationPersistenceService {

    private final TimeDealNotificationLogRepository logRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public List<Long> markAndPersist(
            Long dealId, TimeDealTrigger trigger, String title, String body, String targetId
    ) {
        if (!logRepository.tryMarkAsSent(dealId, trigger)) {
            return List.of();
        }

        List<Long> memberIds = subscriptionRepository.findSubscribedMemberIds(NotificationCategory.TIME_DEAL);
        if (memberIds.isEmpty()) {
            return List.of();
        }

        List<Notification> notifications = memberIds.stream()
                .map(memberId -> new Notification(
                        null, memberId, NotificationDisplayType.TIMEDEAL,
                        title, body, NotificationTargetType.TIMEDEAL, targetId,
                        false, null))
                .toList();
        notificationRepository.saveAll(notifications);
        return memberIds;
    }
}
