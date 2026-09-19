package com.golajugaenyang.order.adapter.in.web.order;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.application.order.port.in.CancelOrderUseCase;
import com.golajugaenyang.order.application.order.port.in.ConfirmOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderStatusController implements OrderStatusControllerDocs {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @Override
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(
        @MemberId Long memberId, @PathVariable Long orderId
    ) {
        confirmOrderUseCase.confirmOrder(orderId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
        @MemberId Long memberId, @PathVariable Long orderId
    ) {
        cancelOrderUseCase.cancelOrder(orderId, memberId);
        return ResponseEntity.noContent().build();
    }
}
