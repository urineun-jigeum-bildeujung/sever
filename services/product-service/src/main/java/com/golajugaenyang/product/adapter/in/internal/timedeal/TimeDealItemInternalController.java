package com.golajugaenyang.product.adapter.in.internal.timedeal;


import com.golajugaenyang.product.adapter.in.internal.timedeal.dto.TimeDealItemsResponse;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealItemInternalLookupUseCase;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/time-deal-items")
@RequiredArgsConstructor
@Validated
public class TimeDealItemInternalController {

    private static final int MAX_BULK_SIZE = 50;

    private final TimeDealItemInternalLookupUseCase timeDealItemInternalLookupUseCase;

    @GetMapping
    public TimeDealItemsResponse getTimeDealItems(
        @RequestParam @NotEmpty @Size(max = MAX_BULK_SIZE) List<Long> ids
    ) {
        return TimeDealItemsResponse
            .from(timeDealItemInternalLookupUseCase.getTimeDealItems(ids));
    }
}
