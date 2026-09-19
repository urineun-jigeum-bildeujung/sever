package com.golajugaenyang.order.adapter.in.web.claim;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.claim.dto.CreateClaimRequest;
import com.golajugaenyang.order.adapter.in.web.claim.dto.CreateClaimResponse;
import com.golajugaenyang.order.application.claim.port.in.CreateClaimUseCase;
import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/orders/{orderId}/claims")
@RequiredArgsConstructor
public class ClaimController implements ClaimControllerDocs {

    private final CreateClaimUseCase createClaimUseCase;

    @Override
    @PostMapping
    public ResponseEntity<CreateClaimResponse> createClaim(
        @MemberId Long memberId,
        @PathVariable Long orderId,
        @Valid @RequestBody CreateClaimRequest request
    ) {
        CreateClaimResult result =
            createClaimUseCase.createClaim(request.toCommand(orderId, memberId));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreateClaimResponse.from(result));
    }
}
