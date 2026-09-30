package com.golajugaenyang.product.adapter.out.persistence.inventory;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.golajugaenyang.common.test.postgres.PostgresIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StockMovementRepositoryImpl.class)
class StockMovementRepositoryImplTest extends PostgresIntegrationTestSupport {

    @Autowired
    private StockMovementRepositoryImpl stockMovementRepository;

    @Test
    @DisplayName("advisory lock 쿼리가 예외 없이 실행된다.")
    void advisory_lock_executes_without_error() {
        assertThatCode(() -> stockMovementRepository.lockOrderItem(1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("존재하지 않는 주문 항목 ID로도 잠금을 획득할 수 있다.")
    void locks_even_for_nonexistent_order_item_id() {
        assertThatCode(() -> stockMovementRepository.lockOrderItem(999_999L))
            .doesNotThrowAnyException();
    }
}
