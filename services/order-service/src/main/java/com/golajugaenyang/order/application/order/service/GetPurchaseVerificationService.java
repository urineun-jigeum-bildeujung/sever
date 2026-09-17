package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.in.GetPurchaseVerificationUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.PurchaseVerificationResult;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GetPurchaseVerificationService implements GetPurchaseVerificationUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseVerificationResult> verify(Long memberId, Long productId) {
        return orderRepositoryPort.findPurchases(memberId, productId).stream()
            .map(p -> new PurchaseVerificationResult(
                p.orderId(), p.orderItemId(), p.orderStatus(),
                p.confirmedAt(), p.itemStatus(), p.orderedAt()))
            .toList();
    }
}
