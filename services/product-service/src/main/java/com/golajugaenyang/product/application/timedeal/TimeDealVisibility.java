package com.golajugaenyang.product.application.timedeal;

import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import java.util.EnumSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeDealVisibility {

    private static final Set<TimeDealStatus> LISTABLE_STATUSES =
        EnumSet.of(TimeDealStatus.ACTIVE, TimeDealStatus.SCHEDULED);

    public static boolean isListable(TimeDealStatus status) {
        return LISTABLE_STATUSES.contains(status);
    }
}
