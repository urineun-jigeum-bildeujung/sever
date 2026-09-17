package com.golajugaenyang.order.adapter.in.internal.order;


import com.golajugaenyang.order.adapter.in.internal.order.dto.OrderPaymentContextResponse;
import com.golajugaenyang.order.application.order.port.in.GetOrderPaymentContextUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class OrderPaymentInternalController {

    private final GetOrderPaymentContextUseCase getOrderPaymentContextUseCase;

    @GetMapping("/{orderId}")
    public OrderPaymentContextResponse getPaymentContext(@PathVariable Long orderId) {
        return OrderPaymentContextResponse.from(
            getOrderPaymentContextUseCase.getPaymentContext(orderId));
    }

}
