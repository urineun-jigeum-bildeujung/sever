package com.golajugaenyang.order.adapter.in.web.order;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.order.dto.CreateOrderRequest;
import com.golajugaenyang.order.adapter.in.web.order.dto.CreateOrderResponse;
import com.golajugaenyang.order.application.order.port.in.CreateOrderUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.CreateOrderResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
        @MemberId Long memberId,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody @Valid CreateOrderRequest request
    ) {
        CreateOrderResult result = createOrderUseCase.createOrder(
            request.toCommand(memberId, idempotencyKey));
        HttpStatus status = result.newlyCreated() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(CreateOrderResponse.from(result));
    }
}
