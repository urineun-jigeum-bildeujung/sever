package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.ConfirmedPurchaseItemResult;
import java.util.List;

public interface GetConfirmedPurchaseItemsUseCase {

    List<ConfirmedPurchaseItemResult> getConfirmedPurchaseItems(Long memberId);
}
