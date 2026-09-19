package com.golajugaenyang.order.application.claim.port.out;

import java.util.List;

public interface OrderItemClaimStatusPort {

    void updateActiveClaimStatus(List<Long> orderItemIds, String claimStatus);
}
