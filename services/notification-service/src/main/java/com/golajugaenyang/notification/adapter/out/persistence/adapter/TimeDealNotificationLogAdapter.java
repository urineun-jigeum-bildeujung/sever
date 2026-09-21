package com.golajugaenyang.notification.adapter.out.persistence.adapter;

import com.golajugaenyang.notification.adapter.out.persistence.repository.TimeDealNotificationLogJpaRepository;
import com.golajugaenyang.notification.domain.entity.enums.TimeDealTrigger;
import com.golajugaenyang.notification.domain.repository.TimeDealNotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TimeDealNotificationLogAdapter implements TimeDealNotificationLogRepository {

    private final TimeDealNotificationLogJpaRepository logJpaRepo;

    @Override
    @Transactional
    public boolean tryMarkAsSent(Long dealId, TimeDealTrigger trigger) {
        return logJpaRepo.insertIfAbsent(dealId, trigger.name()) > 0;
    }
}
