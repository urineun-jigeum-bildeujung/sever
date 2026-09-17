package com.golajugaenyang.order.application.order.service;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.order.port.in.GetOrderPaymentContextUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.OrderPaymentContextResult;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.application.order.port.out.dto.ReservationItem;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderItem;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GetOrderPaymentContextService implements GetOrderPaymentContextUseCase {

    private static final int ORDER_NAME_MAX_LENGTH = 100;

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public OrderPaymentContextResult getPaymentContext(Long orderId) {
        Order order = orderRepositoryPort.findByIdOrNull(orderId);
        if (order == null) {
            throw new AppException(OrderErrorCode.ORDER_NOT_FOUND);
        }
        List<ReservationItem> items = order.getItems().stream()
            .map(OrderItemReservationMapper::toReservationItem)
            .toList();
        return new OrderPaymentContextResult(
            order.getId(), order.getOrderNumber(), order.getMemberId(),
            order.getOrderStatus().name(), order.getTotalAmount(),
            buildOrderName(order.getItems()), items);
    }

    private String buildOrderName(List<OrderItem> items) {
        if (items.isEmpty()) {
            return "";
        }
        String first = items.getFirst().getProductNameSnapshot();
        if (items.size() == 1) {
            return truncate(first, ORDER_NAME_MAX_LENGTH);
        }
        String suffix = " 외 " + (items.size() - 1) + "건";
        int firstMaxLength = Math.max(ORDER_NAME_MAX_LENGTH - suffix.length(), 0);
        return truncate(first, firstMaxLength) + suffix;
    }

    private String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
