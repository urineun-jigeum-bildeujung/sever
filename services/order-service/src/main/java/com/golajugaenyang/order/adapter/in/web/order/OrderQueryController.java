package com.golajugaenyang.order.adapter.in.web.order;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.order.dto.OrderDetailResponse;
import com.golajugaenyang.order.adapter.in.web.order.dto.OrderListResponse;
import com.golajugaenyang.order.application.order.port.in.GetOrderDetailUseCase;
import com.golajugaenyang.order.application.order.port.in.GetOrderListUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.GetOrderListQuery;
import com.golajugaenyang.order.application.order.port.in.dto.OrderListResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderQueryController implements OrderQueryControllerDocs {

    private final GetOrderListUseCase getOrderListUseCase;
    private final GetOrderDetailUseCase getOrderDetailUseCase;

    @Override
    @GetMapping
    public ResponseEntity<OrderListResponse> getOrderList(
        @MemberId Long memberId,
        @RequestParam(required = false) String cursor,
        @Max(10) @Positive int size
    ) {
        OrderListCursorCodec.Cursor decoded = OrderListCursorCodec.decode(cursor);
        GetOrderListQuery query = new GetOrderListQuery(
            memberId,
            decoded != null ? decoded.orderedAt() : null,
            decoded != null ? decoded.orderId() : null,
            size
        );

        OrderListResult result = getOrderListUseCase.getOrderList(query);

        String nextCursor = result.hasNext()
            ? OrderListCursorCodec.encode(result.lastOrderedAt(), result.lastOrderId())
            : null;
        return ResponseEntity.ok(OrderListResponse.from(result, nextCursor));
    }
}
