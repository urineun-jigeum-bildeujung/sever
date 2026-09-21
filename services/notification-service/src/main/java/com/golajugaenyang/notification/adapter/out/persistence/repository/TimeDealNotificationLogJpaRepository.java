package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.TimeDealNotificationLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TimeDealNotificationLogJpaRepository
        extends JpaRepository<TimeDealNotificationLogJpaEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO time_deal_notification_log (deal_id, notification_type, sent_at)
        VALUES (:dealId, :trigger, now())
        ON CONFLICT ON CONSTRAINT uk_time_deal_notification_log_deal_type DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(@Param("dealId") Long dealId, @Param("trigger") String trigger);
}
