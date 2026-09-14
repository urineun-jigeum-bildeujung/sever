package com.golajugaenyang.product.adapter.in.web.timedeal;

import com.golajugaenyang.product.adapter.in.web.timedeal.dto.TimeDealListResponse;
import com.golajugaenyang.product.application.timedeal.port.in.TimeDealListUseCase;
import com.golajugaenyang.product.domain.timedeal.TimeDealStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/time-deals")
@RequiredArgsConstructor
public class TimeDealListController implements TimeDealListControllerDocs {

    private final TimeDealListUseCase timeDealListUseCase;

    @Override
    @GetMapping
    public ResponseEntity<TimeDealListResponse> getTimeDeals(
        @RequestParam TimeDealStatus status
    ) {
        return ResponseEntity.ok(
            TimeDealListResponse.from(timeDealListUseCase.getTimeDeals(status)));
    }
}
