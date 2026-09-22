package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.order.application.claim.port.out.ClaimRepositoryPort;
import com.golajugaenyang.order.application.order.port.in.GetOrderDetailUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.OrderDetailResult;
import com.golajugaenyang.order.domain.claim.OrderClaim;
import com.golajugaenyang.order.domain.claim.OrderClaimItem;
import com.golajugaenyang.order.domain.order.DeliveryAddress;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderDetailService implements GetOrderDetailUseCase {

    private final OrderAccessGuard orderAccessGuard;
    private final ClaimRepositoryPort claimRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResult getOrderDetail(Long orderId, Long memberId) {
        Order order = orderAccessGuard.findOwnedOrder(orderId, memberId);
        List<OrderClaim> claims = claimRepositoryPort.findByOrderId(orderId);

        Map<Long, List<OrderClaim>> claimsByItemId = new HashMap<>();
        for (OrderClaim claim : claims) {
            for (OrderClaimItem claimItem : claim.getItems()) {
                claimsByItemId.computeIfAbsent(
                    claimItem.getOrderItemId(), k -> new ArrayList<>()).add(claim);
            }
        }

        List<OrderDetailResult.ItemDetail> items = order.getItems().stream()
            .map(item -> toItemDetail(item, claimsByItemId))
            .toList();

        OrderDetailResult.PaymentSummary payment = order.getPaidAt() != null
            ? new OrderDetailResult.PaymentSummary(
            order.getPaidAt(), order.getPaymentMethod())
            : null;

        return new OrderDetailResult(
            order.getId(), order.getOrderNumber(),
            order.getOrderStatus().name(), order.getDeliveredAt(),
            order.getProductAmount(), order.getTotalAmount(),
            items, toAddressDetail(order.getDeliveryAddress()),
            order.getDeliveryNote(), payment);
    }

    private OrderDetailResult.ItemDetail toItemDetail(OrderItem item,
        Map<Long, List<OrderClaim>> claimsByItemId) {
        List<OrderDetailResult.ClaimSummary> claims = claimsByItemId.getOrDefault(item.getId(),
                List.of()).stream()
            .map(c -> new OrderDetailResult.ClaimSummary(
                c.getId(), c.getClaimType().name(),
                c.getClaimStatus().name(), c.getRequestedAt(),
                c.getCompletedAt()))
            .toList();
        return new OrderDetailResult.ItemDetail(
            item.getId(), item.getThumbnailUrlSnapshot(), item.getProductNameSnapshot(),
            item.getQuantity(),
            item.getUnitPrice(), item.getItemStatus().name(), claims);
    }

    private OrderDetailResult.DeliveryAddressDetail toAddressDetail(DeliveryAddress address) {
        return new OrderDetailResult.DeliveryAddressDetail(
            address.getReceiver(), address.getReceiverPhone(), address.getZipCode(),
            address.getAddress(), address.getAddressDetail());
    }
}
