package com.golajugaenyang.order.application.order.service;

import com.golajugaenyang.order.application.order.port.in.GetOrderListUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.GetOrderListQuery;
import com.golajugaenyang.order.application.order.port.in.dto.OrderListResult;
import com.golajugaenyang.order.application.order.port.in.dto.OrderListResult.OrderSummary;
import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.domain.order.Order;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderListService implements GetOrderListUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public OrderListResult getOrderList(GetOrderListQuery query) {
        List<Order> fetched = orderRepositoryPort.findByMemberIdWithCursor(
            query.memberId(), query.cursorOrderedAt(), query.cursorOrderId(), query.size() + 1);

        boolean hasNext = fetched.size() > query.size();
        List<Order> pageOrders = hasNext ? fetched.subList(0, query.size()) : fetched;

        List<OrderSummary> summaries = pageOrders.stream().map(this::toSummary).toList();

        Order last = pageOrders.isEmpty() ? null : pageOrders.getLast();
        return new OrderListResult(
            summaries, hasNext,
            last != null ? last.getOrderedAt() : null,
            last != null ? last.getId() : null);
    }

    private OrderListResult.OrderSummary toSummary(Order order) {
        List<OrderListResult.ItemSummary> items = order.getItems().stream()
            .map(i -> new OrderListResult.ItemSummary(
                i.getId(), i.getProductId(),
                i.getThumbnailUrlSnapshot(), i.getProductNameSnapshot(),
                i.getQuantity(), i.lineAmount()))
            .toList();
        return new OrderListResult.OrderSummary(
            order.getId(), order.getOrderNumber(), order.getOrderedAt(),
            order.getOrderStatus().name(),
            order.getTotalAmount(), items);
    }
}
