package com.golajugaenyang.order.application.claim.service;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.claim.port.in.CreateClaimUseCase;
import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimCommand;
import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimResult;
import com.golajugaenyang.order.application.claim.port.out.ClaimRepositoryPort;
import com.golajugaenyang.order.application.claim.port.out.OrderItemClaimStatusPort;
import com.golajugaenyang.order.application.claim.port.out.OrderLookupPort;
import com.golajugaenyang.order.application.claim.port.out.dto.ClaimableOrder;
import com.golajugaenyang.order.domain.claim.ClaimType;
import com.golajugaenyang.order.domain.claim.OrderClaim;
import com.golajugaenyang.order.domain.claim.OrderClaimItem;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CreateClaimService implements CreateClaimUseCase {

    private final OrderLookupPort orderLookupPort;
    private final ClaimRepositoryPort claimRepositoryPort;
    private final OrderItemClaimStatusPort orderItemClaimStatusPort;

    @Override
    @Transactional
    public CreateClaimResult createClaim(CreateClaimCommand command) {
        ClaimableOrder order = orderLookupPort
            .findClaimableOrder(command.orderId(), command.memberId());
        if (!order.claimable()) {
            throw new AppException(OrderErrorCode.ORDER_NOT_CLAIMABLE);
        }

        ClaimType claimType;
        try {
            claimType = ClaimType.fromCode(command.claimType());
        } catch (IllegalArgumentException e) {
            throw new AppException(OrderErrorCode.INVALID_CLAIM_TYPE);
        }

        List<Long> requestedItemIds = command.items().stream()
            .map(CreateClaimCommand.Item::orderItemId).toList();
        if (claimRepositoryPort
            .existsActiveClaimForItems(command.orderId(), requestedItemIds)) {
            throw new AppException(OrderErrorCode.CLAIM_ALREADY_IN_PROGRESS);
        }

        OrderClaim claim = OrderClaim.request(
            command.orderId(), claimType, command.reason(), command.imageUrls());

        Map<Long, Integer> effectiveQuantityByItemId = order.items().stream()
            .collect(Collectors.toMap(
                ClaimableOrder.Item::orderItemId,
                ClaimableOrder.Item::effectiveQuantity));

        for (CreateClaimCommand.Item requested : command.items()) {
            Integer effectiveQuantity = effectiveQuantityByItemId.get(requested.orderItemId());
            if (effectiveQuantity == null) {
                throw new AppException(OrderErrorCode.ORDER_ITEM_NOT_FOUND);
            }
            if (requested.quantity() > effectiveQuantity) {
                throw new AppException(OrderErrorCode.CLAIM_ITEM_QUANTITY_EXCEEDED);
            }
            claim.addItem(OrderClaimItem.of(requested.orderItemId(), requested.quantity()));
        }

        OrderClaim saved = claimRepositoryPort.save(claim);

        List<Long> claimedItemIds = command.items().stream()
            .map(CreateClaimCommand.Item::orderItemId).toList();
        orderItemClaimStatusPort.updateActiveClaimStatus(
            claimedItemIds, saved.getClaimStatus().name());

        return CreateClaimResult.from(saved);
    }
}
