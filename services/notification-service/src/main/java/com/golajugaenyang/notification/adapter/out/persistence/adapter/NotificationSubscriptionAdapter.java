package com.golajugaenyang.notification.adapter.out.persistence.adapter;

import com.golajugaenyang.notification.adapter.out.persistence.entity.NotificationSubscriptionJpaEntity;
import com.golajugaenyang.notification.adapter.out.persistence.repository.NotificationSubscriptionJpaRepository;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import com.golajugaenyang.notification.domain.repository.NotificationSubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationSubscriptionAdapter implements NotificationSubscriptionRepository {

    private final NotificationSubscriptionJpaRepository subscriptionJpaRepo;

    @Override
    @Transactional
    public void upsert(Long memberId, NotificationCategory category, boolean subscribed) {
        subscriptionJpaRepo.findByMemberIdAndCategory(memberId, category)
                .ifPresentOrElse(
                        existing -> existing.setSubscribed(subscribed),
                        () -> subscriptionJpaRepo.save(NotificationSubscriptionJpaEntity.builder()
                                .memberId(memberId)
                                .category(category)
                                .subscribed(subscribed)
                                .build())
                );
    }

    @Override
    public boolean isSubscribed(Long memberId, NotificationCategory category) {
        return subscriptionJpaRepo.findByMemberIdAndCategory(memberId, category)
                .map(NotificationSubscriptionJpaEntity::isSubscribed)
                .orElse(false);
    }

    @Override
    public List<Long> findSubscribedMemberIds(NotificationCategory category) {
        return subscriptionJpaRepo.findByCategoryAndSubscribedTrue(category).stream()
                .map(NotificationSubscriptionJpaEntity::getMemberId)
                .toList();
    }
}
