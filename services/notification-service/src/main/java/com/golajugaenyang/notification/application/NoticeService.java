package com.golajugaenyang.notification.application;

import com.golajugaenyang.notification.adapter.out.push.FcmPushSender;
import com.golajugaenyang.notification.domain.entity.Notification;
import com.golajugaenyang.notification.domain.entity.enums.NotificationDisplayType;
import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import com.golajugaenyang.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 공지(NOTICE)는 구독 여부와 무관하게, FCM 토큰을 등록한 적 있는 모든 회원에게 발송한다.
 * (TIME_DEAL처럼 opt-in 구독 대상이 아니라 시스템 공지이기 때문)
 */
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final FcmPushSender fcmPushSender;

    public void broadcastNotice(String title, String body) {
        List<Long> memberIds = fcmTokenRepository.findAllMemberIds();
        if (memberIds.isEmpty()) {
            return;
        }

        List<Notification> notifications = memberIds.stream()
                .map(memberId -> new Notification(
                        null, memberId, NotificationDisplayType.NOTICE,
                        title, body, null, null, false, null))
                .toList();
        notificationRepository.saveAll(notifications);

        Map<Long, List<String>> tokensByMember = fcmTokenRepository.findTokensByMemberIds(memberIds);
        for (Long memberId : memberIds) {
            for (String token : tokensByMember.getOrDefault(memberId, List.of())) {
                fcmPushSender.send(token, title, body, null, null);
            }
        }
    }
}
