package com.golajugaenyang.order.application.claim.port.out;

import com.golajugaenyang.order.application.claim.port.out.dto.ClaimableOrder;

public interface OrderLookupPort {

    ClaimableOrder findClaimableOrder(Long orderId, Long memberId);
}
