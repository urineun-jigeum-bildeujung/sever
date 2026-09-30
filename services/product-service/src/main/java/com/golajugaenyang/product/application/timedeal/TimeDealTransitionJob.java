package com.golajugaenyang.product.application.timedeal;


import com.golajugaenyang.product.application.timedeal.port.out.TimeDealTransitionRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealTransitionJob {

    private final TimeDealTransitionRepository timeDealTransitionRepository;

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void transition() {
        OffsetDateTime now = OffsetDateTime.now();
        int activated = timeDealTransitionRepository.activateScheduledDeals(now);
        int ended = timeDealTransitionRepository.endActiveDeals(now);

        if (activated > 0 || ended > 0) {
            log.info("[TimeDealTransition] 진행중 전환={}건, 종료 전환={}건", activated, ended);
        }
    }
}
