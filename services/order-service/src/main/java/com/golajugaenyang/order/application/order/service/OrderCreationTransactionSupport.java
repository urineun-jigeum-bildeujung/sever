package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderCommand;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;
import com.golajugaenyang.order.application.order.port.out.AddressLookupPort;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.application.order.port.out.ProductCatalogPort;
import com.golajugaenyang.order.application.order.port.out.dto.AddressInfo;
import com.golajugaenyang.order.application.order.port.out.dto.CatalogItem;
import com.golajugaenyang.order.application.order.port.out.dto.CatalogKey;
import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import com.golajugaenyang.order.config.OrderReservationProperties;
import com.golajugaenyang.order.domain.order.DeliveryAddress;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderItem;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class OrderCreationTransactionSupport {

    private static final BigDecimal FIXED_SHIPPING_FEE = BigDecimal.valueOf(3000);

    private final ProductCatalogPort productCatalogPort;
    private final AddressLookupPort addressLookupPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderReservationProperties reservationProperties;

    @Transactional(readOnly = true)
    public CreateOrderResult findExistingResult(Long memberId, String idempotencyKey) {
        if (!orderRepositoryPort.existsByMemberIdAndIdempotencyKey(memberId, idempotencyKey)) {
            return null;
        }
        Order existing = orderRepositoryPort.findByMemberIdAndIdempotencyKey(
            memberId, idempotencyKey);
        return CreateOrderResult.from(existing, false);
    }

    @Transactional
    public PendingOrderCreation persistPendingOrder(CreateOrderCommand command) {
        AddressInfo addressInfo = addressLookupPort.lookup(command.addressId(), command.memberId());

        List<Long> productIds = command.items().stream()
            .filter(i -> !i.isTimeDeal())
            .map(CreateOrderCommand.Item::productId)
            .toList();
        List<Long> dealItemIds = command.items().stream()
            .filter(CreateOrderCommand.Item::isTimeDeal)
            .map(CreateOrderCommand.Item::dealItemId)
            .toList();

        List<CatalogItem> catalogItems = productCatalogPort.lookup(productIds, dealItemIds);
        Map<CatalogKey, CatalogItem> catalogByKey = catalogItems.stream()
            .collect(
                Collectors.toMap(c ->
                    new CatalogKey(c.isTimeDeal(), c.referenceId()), c -> c));

        DeliveryAddress deliveryAddress = DeliveryAddress.from(command.addressId(), addressInfo);

        String deliveryNote = (command.deliveryNote() != null && !command.deliveryNote().isBlank())
            ? command.deliveryNote() : addressInfo.deliveryNote();

        Order order = Order.createPending(
            orderRepositoryPort.generateOrderNumber(), command.idempotencyKey(), command.memberId(),
            deliveryAddress, deliveryNote, reservationProperties.ttl());

        BigDecimal productAmount = BigDecimal.ZERO;
        for (CreateOrderCommand.Item requested : command.items()) {
            CatalogKey key = requested.isTimeDeal()
                ? new CatalogKey(true, requested.dealItemId())
                : new CatalogKey(false, requested.productId());
            CatalogItem catalog = catalogByKey.get(key);
            if (catalog == null) {
                throw new AppException(OrderErrorCode.PRODUCT_NOT_FOUND);
            }
            if (!catalog.purchasable()) {
                throw new AppException(OrderErrorCode.PRODUCT_NOT_PURCHASABLE);
            }
            OrderItem orderItem = OrderItem.fromCatalogSnapshot(catalog, requested.quantity());
            order.addItem(orderItem);
            productAmount = productAmount.add(orderItem.lineAmount());
        }
        order.applyAmounts(productAmount, FIXED_SHIPPING_FEE);

        Order savedOrder = orderRepositoryPort.save(order);

        List<ReservationItem> reservationItems = savedOrder.getItems().stream()
            .map(this::toReservationItem)
            .toList();
        CreateOrderResult result = CreateOrderResult.from(savedOrder, true);

        return new PendingOrderCreation(savedOrder.getId(), reservationItems, result);
    }

    @Transactional
    public void confirmReservation(Long orderId) {
        orderRepositoryPort.findById(orderId).confirmReservation();
    }

    @Transactional
    public void failReservation(Long orderId) {
        orderRepositoryPort.findById(orderId).failReservation();
    }

    private ReservationItem toReservationItem(OrderItem item) {
        boolean isTimeDeal = item.getDealItemId() != null;
        String subjectType = isTimeDeal ? "TIME_DEAL_ITEM" : "PRODUCT";
        Long subjectId = isTimeDeal ? item.getDealItemId() : item.getProductId();
        return new ReservationItem(item.getId(), subjectType, subjectId, item.getQuantity());
    }
}
