package com.golajugaenyang.product.application.timedeal.port.out;

import com.golajugaenyang.product.application.timedeal.port.out.dto.TimeDealPricingProjection;
import java.util.Optional;

public interface TimeDealPricingRepository {

    Optional<TimeDealPricingProjection> findByTimeDealItemId(Long timeDealItemId);
}
