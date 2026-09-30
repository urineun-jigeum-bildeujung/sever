package com.golajugaenyang.product.application.timedeal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.product.application.timedeal.port.out.TimeDealTransitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimeDealTransitionJobTest {

    @Mock
    private TimeDealTransitionRepository timeDealTransitionRepository;

    @InjectMocks
    private TimeDealTransitionJob timeDealTransitionJob;

    @Test
    @DisplayName("매 실행마다 진행중 전환과 종료 전환을 모두 시도한다.")
    void attempts_both_activation_and_ending_on_every_run() {
        when(timeDealTransitionRepository.activateScheduledDeals(any())).thenReturn(1);
        when(timeDealTransitionRepository.endActiveDeals(any())).thenReturn(2);

        timeDealTransitionJob.transition();

        verify(timeDealTransitionRepository).activateScheduledDeals(any());
        verify(timeDealTransitionRepository).endActiveDeals(any());
    }
}
