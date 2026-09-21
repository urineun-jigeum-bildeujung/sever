package com.golajugaenyang.notification.application;

import com.golajugaenyang.notification.adapter.out.client.ProductClient;
import com.golajugaenyang.notification.adapter.out.client.dto.TimeDealListResponse.DealGroupResponse;
import com.golajugaenyang.notification.adapter.out.push.FcmPushSender;
import com.golajugaenyang.notification.domain.entity.Notification;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import com.golajugaenyang.notification.domain.entity.enums.NotificationDisplayType;
import com.golajugaenyang.notification.domain.entity.enums.NotificationTargetType;
import com.golajugaenyang.notification.domain.entity.enums.TimeDealTrigger;
import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import com.golajugaenyang.notification.domain.repository.NotificationRepository;
import com.golajugaenyang.notification.domain.repository.NotificationSubscriptionRepository;
import com.golajugaenyang.notification.domain.repository.TimeDealNotificationLogRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 타임딜 시작 10분 전 / 시작 시점 / 시작 30분 후, 3개 시점에 구독자에게 알림을 보낸다.
 * 폴링마다 "트리거 시점이 지났는지"만 확인하고, 실제 중복 발송 방지는
 * {@link TimeDealNotificationLogRepository}의 원자적 insert로 처리한다
 * (스케줄러가 늦게 뜨거나 폴링을 놓쳐도 다음 폴링에서 안전하게 따라잡는다).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealNotificationScheduler {

    private static final Duration UPCOMING_OFFSET = Duration.ofMinutes(10);
    private static final Duration ONGOING_OFFSET = Duration.ofMinutes(30);

    private final ProductClient productClient;
    private final TimeDealNotificationLogRepository logRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final FcmPushSender fcmPushSender;

    @Scheduled(fixedRate = 60_000)
    public void checkTimeDeals() {
        OffsetDateTime now = OffsetDateTime.now();
        for (DealGroupResponse deal : fetchDeals()) {
            checkAndNotify(deal, now);
        }
    }

    private List<DealGroupResponse> fetchDeals() {
        List<DealGroupResponse> deals = new ArrayList<>();
        deals.addAll(safeGetDeals("SCHEDULED"));
        deals.addAll(safeGetDeals("ACTIVE"));
        return deals;
    }

    private List<DealGroupResponse> safeGetDeals(String status) {
        try {
            var result = productClient.getTimeDeals(status);
            return result.deals() == null ? List.of() : result.deals();
        } catch (Exception e) {
            log.warn("타임딜 목록 조회 실패: status={}, error={}", status, e.getMessage());
            return List.of();
        }
    }

    private void checkAndNotify(DealGroupResponse deal, OffsetDateTime now) {
        OffsetDateTime startAt = deal.startAt();
        OffsetDateTime endAt = deal.endAt();
        if (startAt == null) {
            return;
        }

        if (now.isBefore(startAt) && !now.isBefore(startAt.minus(UPCOMING_OFFSET))) {
            tryNotify(deal, TimeDealTrigger.UPCOMING,
                    "타임딜 시작 10분 전!", deal.dealName() + " 타임딜이 곧 시작해요.");
        }

        if (!now.isBefore(startAt) && isOngoing(now, endAt)) {
            tryNotify(deal, TimeDealTrigger.START,
                    "타임딜 시작!", deal.dealName() + " 타임딜이 지금 시작했어요.");
        }

        if (!now.isBefore(startAt.plus(ONGOING_OFFSET)) && isOngoing(now, endAt)) {
            tryNotify(deal, TimeDealTrigger.ONGOING,
                    "타임딜 진행중", deal.dealName() + " 타임딜, 아직 늦지 않았어요.");
        }
    }

    private boolean isOngoing(OffsetDateTime now, OffsetDateTime endAt) {
        return endAt == null || now.isBefore(endAt);
    }

    private void tryNotify(DealGroupResponse deal, TimeDealTrigger trigger, String title, String body) {
        String targetId = deal.dealId().toString();
        List<Long> memberIds = markAndPersist(deal.dealId(), trigger, title, body, targetId);
        if (memberIds.isEmpty()) {
            return;
        }

        Map<Long, List<String>> tokensByMember = fcmTokenRepository.findTokensByMemberIds(memberIds);
        for (Long memberId : memberIds) {
            for (String token : tokensByMember.getOrDefault(memberId, List.of())) {
                fcmPushSender.send(token, title, body, NotificationTargetType.TIMEDEAL.name(), targetId);
            }
        }
    }

    private List<Long> markAndPersist(
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
