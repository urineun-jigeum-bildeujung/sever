package com.golajugaenyang.order.application.claim.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimCommand;
import com.golajugaenyang.order.application.claim.port.out.ClaimRepositoryPort;
import com.golajugaenyang.order.application.claim.port.out.OrderItemClaimStatusPort;
import com.golajugaenyang.order.application.claim.port.out.OrderLookupPort;
import com.golajugaenyang.order.application.claim.port.out.dto.ClaimableOrder;
import com.golajugaenyang.order.error.OrderErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CreateClaimServiceTest {

    @Mock
    private OrderLookupPort orderLookupPort;

    @Mock
    private ClaimRepositoryPort claimRepositoryPort;

    @Mock
    private OrderItemClaimStatusPort orderItemClaimStatusPort;

    @InjectMocks
    private CreateClaimService createClaimService;


    @Test
    @DisplayName("이미 활성화된 문의가 있는 품목이 포함되면 전체 신청이 거부된다.")
    void create_claim_throws_when_any_item_already_claimed() {
        ClaimableOrder order = new ClaimableOrder(
            10L, true,
            List.of(
                new ClaimableOrder.Item(101L, 1),
                new ClaimableOrder.Item(102L, 1)));
        when(orderLookupPort.findClaimableOrder(10L, 1L)).thenReturn(order);
        when(orderItemClaimStatusPort.claimForNewRequest(any(), any()))
            .thenReturn(List.of(102L));

        CreateClaimCommand command = new CreateClaimCommand(
            10L, 1L, "RETURN", null,
            List.of(
                new CreateClaimCommand.Item(101L, 1),
                new CreateClaimCommand.Item(102L, 1)), null);

        assertThatThrownBy(() -> createClaimService.createClaim(command))
            .isInstanceOf(AppException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", OrderErrorCode.CLAIM_ALREADY_IN_PROGRESS);

        verify(claimRepositoryPort, never()).save(any());
    }

}
