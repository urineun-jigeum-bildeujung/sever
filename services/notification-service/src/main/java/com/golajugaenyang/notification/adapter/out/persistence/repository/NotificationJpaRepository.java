package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.NotificationJpaEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {

    List<NotificationJpaEntity> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("""
        update NotificationJpaEntity n set n.read = true
        where n.id = :notificationId and n.memberId = :memberId
        """)
    int markAsRead(@Param("memberId") Long memberId, @Param("notificationId") Long notificationId);
}
