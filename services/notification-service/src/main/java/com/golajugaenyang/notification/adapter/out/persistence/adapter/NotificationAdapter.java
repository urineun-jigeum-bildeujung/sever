package com.golajugaenyang.notification.adapter.out.persistence.adapter;

import com.golajugaenyang.notification.adapter.out.persistence.mapper.NotificationMapper;
import com.golajugaenyang.notification.adapter.out.persistence.repository.NotificationJpaRepository;
import com.golajugaenyang.notification.domain.entity.Notification;
import com.golajugaenyang.notification.domain.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepo;

    @Override
    public void saveAll(List<Notification> notifications) {
        notificationJpaRepo.saveAll(notifications.stream().map(NotificationMapper::toJpaEntity).toList());
    }

    @Override
    public List<Notification> findByMemberId(Long memberId, int page, int size) {
        return notificationJpaRepo.findByMemberIdOrderByCreatedAtDesc(memberId, PageRequest.of(page, size))
                .stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public int markAsRead(Long memberId, Long notificationId) {
        return notificationJpaRepo.markAsRead(memberId, notificationId);
    }
}
