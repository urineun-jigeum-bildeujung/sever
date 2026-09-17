package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.PurchaseVerificationResult;
import java.util.List;


public interface GetPurchaseVerificationUseCase {

    List<PurchaseVerificationResult> verify(Long memberId, Long productId);
}
