package com.golajugaenyang.order.application.claim.port.out;

import java.util.List;

public interface OrderItemClaimStatusPort {

    List<Long> claimForNewRequest(List<Long> orderItemIds, String claimStatus);
}
