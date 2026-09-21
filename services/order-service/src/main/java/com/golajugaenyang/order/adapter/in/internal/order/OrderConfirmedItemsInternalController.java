package com.golajugaenyang.order.adapter.in.internal.order;


import com.golajugaenyang.order.adapter.in.internal.order.dto.ConfirmedPurchaseItemResponse;
import com.golajugaenyang.order.adapter.in.internal.order.dto.ConfirmedPurchaseItemsResponse;
import com.golajugaenyang.order.application.order.port.in.GetConfirmedPurchaseItemsUseCase;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orders/confirmed-items")
@RequiredArgsConstructor
@Validated
public class OrderConfirmedItemsInternalController {

    private final GetConfirmedPurchaseItemsUseCase getConfirmedPurchaseItemsUseCase;

    @GetMapping
    public ConfirmedPurchaseItemsResponse getConfirmedItems(@RequestParam @NotNull Long memberId) {
        List<ConfirmedPurchaseItemResponse> items = getConfirmedPurchaseItemsUseCase
            .getConfirmedPurchaseItems(memberId).stream()
            .map(ConfirmedPurchaseItemResponse::from)
            .toList();
        return new ConfirmedPurchaseItemsResponse(items);
    }
}
