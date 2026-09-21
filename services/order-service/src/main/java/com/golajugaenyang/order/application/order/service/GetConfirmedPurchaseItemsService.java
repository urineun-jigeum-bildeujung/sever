package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.order.application.order.port.in.GetConfirmedPurchaseItemsUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.ConfirmedPurchaseItemResult;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetConfirmedPurchaseItemsService implements GetConfirmedPurchaseItemsUseCase {

    private static final int MAX_RESULT_SIZE = 500;

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ConfirmedPurchaseItemResult> getConfirmedPurchaseItems(Long memberId) {
        return orderRepositoryPort
            .findConfirmedPurchaseItems(memberId, MAX_RESULT_SIZE).stream()
            .map(i -> new ConfirmedPurchaseItemResult(
                i.productId(), i.orderId(), i.orderItemId(),
                i.orderStatus(), i.itemStatus(), i.confirmedAt()))
            .toList();
    }
}
