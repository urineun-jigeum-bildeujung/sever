package com.golajugaenyang.product.application.inventory;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.product.application.inventory.port.out.ProductStatusCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ProductStatusSyncerTest {

    @Mock
    private ProductStatusCommandRepository productStatusCommandRepository;

    @InjectMocks
    private ProductStatusSyncer productStatusSyncer;

    @Test
    @DisplayName("SOLD_OUT 전환이 성공하면 ON_SALE 전환은 시도하지 않는다.")
    void does_not_attempt_on_sale_when_sold_out_succeeds() {
        when(productStatusCommandRepository
            .markSoldOutIfStillOutOfStock(1L)).thenReturn(1);

        productStatusSyncer.sync(1L);

        verify(productStatusCommandRepository, never()).markOnSaleIfStillInStock(1L);
    }

    @Test
    @DisplayName("SOLD_OUT 전환 조건이 안 맞으면 ON_SALE 전환을 시도한다.")
    void attempts_on_sale_when_sold_out_condition_not_met() {
        when(productStatusCommandRepository
            .markSoldOutIfStillOutOfStock(1L)).thenReturn(0);

        productStatusSyncer.sync(1L);

        verify(productStatusCommandRepository).markOnSaleIfStillInStock(1L);
    }
}
