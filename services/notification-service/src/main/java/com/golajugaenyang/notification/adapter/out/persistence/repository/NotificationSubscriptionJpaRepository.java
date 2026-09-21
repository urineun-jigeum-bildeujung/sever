package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.NotificationSubscriptionJpaEntity;
import com.golajugaenyang.notification.domain.entity.enums.NotificationCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationSubscriptionJpaRepository
        extends JpaRepository<NotificationSubscriptionJpaEntity, Long> {

    Optional<NotificationSubscriptionJpaEntity> findByMemberIdAndCategory(
            Long memberId, NotificationCategory category);

    List<NotificationSubscriptionJpaEntity> findByCategoryAndSubscribedTrue(NotificationCategory category);

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO notification_subscription (member_id, category, subscribed, created_at, updated_at)
        VALUES (:memberId, :category, :subscribed, now(), now())
        ON CONFLICT ON CONSTRAINT uk_notification_subscription_member_category DO UPDATE
        SET subscribed = EXCLUDED.subscribed, updated_at = now()
        """, nativeQuery = true)
    void upsert(
            @Param("memberId") Long memberId,
            @Param("category") String category,
            @Param("subscribed") boolean subscribed);
}
