package com.golajugaenyang.order.adapter.in.internal.order;


import com.golajugaenyang.order.adapter.in.internal.order.dto.PurchaseVerificationItemResponse;
import com.golajugaenyang.order.adapter.in.internal.order.dto.PurchaseVerificationResponse;
import com.golajugaenyang.order.application.order.port.in.GetPurchaseVerificationUseCase;
import com.golajugaenyang.order.application.order.port.in.dto.PurchaseVerificationResult;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orders/purchase-verification")
@RequiredArgsConstructor
@Validated
public class OrderPurchaseVerificationInternalController {

    private final GetPurchaseVerificationUseCase getPurchaseVerificationUseCase;

    @GetMapping
    public PurchaseVerificationResponse verify(
        @RequestParam @NotNull Long memberId,
        @RequestParam @NotNull Long productId
    ) {
        List<PurchaseVerificationResult> results =
            getPurchaseVerificationUseCase.verify(memberId, productId);
        List<PurchaseVerificationItemResponse> items = results.stream()
            .map(PurchaseVerificationItemResponse::from)
            .toList();
        return new PurchaseVerificationResponse(items);
    }
}
