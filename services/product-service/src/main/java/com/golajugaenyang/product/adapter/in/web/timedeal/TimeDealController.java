package com.golajugaenyang.product.adapter.in.web.timedeal;


import com.golajugaenyang.product.adapter.in.web.product.dto.ProductDetailResponse;
import com.golajugaenyang.product.adapter.in.web.timedeal.dto.TimeDealListResponse;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealDetailUseCase;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealListUseCase;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/time-deals")
@RequiredArgsConstructor
@Validated
public class TimeDealController implements TimeDealControllerDocs {

    private final TimeDealListUseCase timeDealListUseCase;
    private final TimeDealDetailUseCase timeDealDetailUseCase;

    @Override
    @GetMapping
    public ResponseEntity<TimeDealListResponse> getTimeDeals(
        @RequestParam TimeDealStatus status
    ) {
        return ResponseEntity.ok(
            TimeDealListResponse.from(timeDealListUseCase.getTimeDeals(status)));
    }

    @Override
    @GetMapping("/items/{timeDealItemId}")
    public ResponseEntity<ProductDetailResponse> getTimeDealDetail(
        @PathVariable @Min(1) Long timeDealItemId
    ) {
        return ResponseEntity.ok(
            ProductDetailResponse.from(timeDealDetailUseCase.getTimeDealDetail(timeDealItemId)));
    }
}
