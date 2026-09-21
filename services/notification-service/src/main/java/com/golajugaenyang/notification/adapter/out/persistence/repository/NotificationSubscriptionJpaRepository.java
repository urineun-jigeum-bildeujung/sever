package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.NotificationSubscriptionJpaEntity;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSubscriptionJpaRepository
        extends JpaRepository<NotificationSubscriptionJpaEntity, Long> {

    Optional<NotificationSubscriptionJpaEntity> findByMemberIdAndCategory(
            Long memberId, NotificationCategory category);

    List<NotificationSubscriptionJpaEntity> findByCategoryAndSubscribedTrue(NotificationCategory category);
}
