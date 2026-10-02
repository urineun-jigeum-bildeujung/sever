package com.golajugaenyang.product.application.inventory;


import static org.assertj.core.api.Assertions.assertThat;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.test.postgres.PostgresIntegrationTestSupport;
import com.golajugaenyang.product.adapter.out.persistence.inventory.InventoryCommandRepositoryImpl;
import com.golajugaenyang.product.adapter.out.persistence.inventory.StockMovementRepositoryImpl;
import com.golajugaenyang.product.adapter.out.persistence.timedeal.TimeDealStockCommandRepositoryImpl;
import com.golajugaenyang.product.application.inventory.concurrency.ConcurrencyTestHarness;
import com.golajugaenyang.product.application.inventory.concurrency.ConcurrentOutcome;
import com.golajugaenyang.product.application.inventory.port.in.InventoryCommandUseCase;
import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import com.golajugaenyang.product.config.JpaAuditingTestConfig;
import com.golajugaenyang.product.config.MeterRegistryTestConfig;
import com.golajugaenyang.product.config.QuerydslTestConfig;
import com.golajugaenyang.product.domain.inventory.Inventory;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import com.golajugaenyang.product.error.ProductErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Tag("concurrency")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    InventoryCommandService.class,
    InventoryCommandRepositoryImpl.class,
    TimeDealStockCommandRepositoryImpl.class,
    StockMovementRepositoryImpl.class,
    QuerydslTestConfig.class,
    MeterRegistryTestConfig.class,
    JpaAuditingTestConfig.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("재고 커맨드 동시성 통합 테스트")
public class InventoryCommandConcurrencyTest extends PostgresIntegrationTestSupport {

    @Autowired
    private InventoryCommandUseCase inventoryCommandUseCase;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate tx;

    @BeforeEach
    void setUp() {
        tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            em.getEntityManager().createQuery("DELETE FROM StockMovement").executeUpdate();
            em.getEntityManager().createQuery("DELETE FROM Inventory").executeUpdate();
        });
    }

    private void seedInventory(Long productId, int totalStock) {
        tx.executeWithoutResult(status -> em.getEntityManager()
            .persist(Inventory.initialize(productId, totalStock)));
    }

    private Inventory findInventory(Long productId) {
        return tx.execute(status -> em.getEntityManager()
            .find(Inventory.class, productId));
    }

    private long countMovements(Long orderItemId) {
        return tx.execute(status -> em.getEntityManager()
            .createQuery("SELECT COUNT(m) FROM StockMovement m WHERE m.orderItemId = :id",
                Long.class)
            .setParameter("id", orderItemId)
            .getSingleResult());
    }

    @Nested
    @DisplayName("동일 상품에 동일 유형(RESERVE) 요청이 동시에 들어오는 경우")
    class SameProductAndType_Reserve {

        private static final Long PRODUCT_ID = 1L;

        @Test
        @DisplayName("재고가 충분하면 모든 예약이 성공하고 예약 수량이 정확히 누적된다.")
        void succeeds_for_all_when_stock_is_sufficient() {
            // given: 재고 100개짜리 상품에, 서로 다른 주문 10건이 각 2개씩 예약을 시도한다.
            seedInventory(PRODUCT_ID, 100);
            List<Runnable> tasks = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> (Runnable) () -> inventoryCommandUseCase.reserveBulk(
                    List.of(
                        new ReserveItemCommand((long) i, StockSubjectType.PRODUCT, PRODUCT_ID, 2))))
                .toList();

            // when: 10건을 동시에 실행한다.
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 전부 성공하고, 예약 수량은 유실 없이 정확히 20(=10건 × 2개)이다.
            assertThat(outcomes).allMatch(ConcurrentOutcome::success);
            assertThat(findInventory(PRODUCT_ID).getReservedStock()).isEqualTo(20);
        }

        @Test
        @DisplayName("재고보다 많은 동시 요청이 오면 가용 재고만큼만 성공하고 나머지는 재고 부족으로 실패한다.")
        void fails_only_for_excess_when_stock_is_insufficient() {
            // given: 재고 10개짜리 상품에, 서로 다른 주문 10건이 각 2개씩(합계 20개) 예약을 시도한다.
            seedInventory(PRODUCT_ID, 10);
            List<Runnable> tasks = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> (Runnable) () -> inventoryCommandUseCase.reserveBulk(
                    List.of(
                        new ReserveItemCommand((long) i, StockSubjectType.PRODUCT, PRODUCT_ID, 2))))
                .toList();

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 정확히 5건만 성공(10개÷2개=5건), 나머지 5건은 INSUFFICIENT_STOCK으로 실패한다.
            //       재고는 과다 예약(10 초과)되지 않는다
            assertThat(outcomes).filteredOn(ConcurrentOutcome::success).hasSize(5);
            assertThat(outcomes).filteredOn(o -> !o.success())
                .hasSize(5)
                .allSatisfy(o -> assertThat(o.exception())
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.INSUFFICIENT_STOCK));
            assertThat(findInventory(PRODUCT_ID).getReservedStock()).isEqualTo(10);
        }

        @Test
        @DisplayName("동일한 예약 이벤트가 중복 도착해도 재고는 한 번만 반영된다")
        void ConcurrentRequestsForSameProductDifferentTypes() {
            // given: 재고 10개짜리 상품에, 같은 orderItemId·같은 내용의 예약 요청 5건이 동시에 도착한다.
            //        (Kafka의 최소 한 번 전달로 같은 이벤트가 중복 발행되는 상황을 재현)
            seedInventory(PRODUCT_ID, 10);
            List<Runnable> tasks = IntStream.range(0, 5)
                .mapToObj(i -> (Runnable) () -> inventoryCommandUseCase.reserveBulk(
                    List.of(new ReserveItemCommand(1L, StockSubjectType.PRODUCT, PRODUCT_ID, 2))))
                .toList();

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 전부 "성공"으로 끝나지만(멱등), 실제 재고는 2만 반영되고 이력도 1건뿐이다.
            assertThat(outcomes).allMatch(ConcurrentOutcome::success);
            assertThat(findInventory(PRODUCT_ID).getReservedStock()).isEqualTo(2);
            assertThat(countMovements(1L)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("동일 상품에 서로 다른 유형의 요청이 동시에 들어오는 경우")
    class SameProductDifferentTypes {

        private static final Long PRODUCT_ID = 1L;

        @Test
        @DisplayName("서로 다른 주문 항목의 확정과 해제가 동시에 발생해도 재고가 정확히 반영된다.")
        void reflects_confirm_and_release_for_different_orders() {
            // given: 재고 100개짜리 상품에, 이미 예약된 주문 항목 20건(각 2개, 합계 40개)이 있다.
            //        그중 10건은 결제 확정(CONFIRM), 10건은 결제 실패로 인한 해제(RELEASE) 대상이다.
            seedInventory(PRODUCT_ID, 100);
            List<Long> toConfirm = LongStream.rangeClosed(1, 10).boxed().toList();
            List<Long> toRelease = LongStream.rangeClosed(11, 20).boxed().toList();
            Stream.concat(toConfirm.stream(), toRelease.stream()).forEach(id ->
                inventoryCommandUseCase.reserveBulk(
                    List.of(new ReserveItemCommand(id, StockSubjectType.PRODUCT, PRODUCT_ID, 2))));

            List<Runnable> tasks = new ArrayList<>();
            toConfirm.forEach(id -> tasks.add(() ->
                inventoryCommandUseCase.confirm(StockSubjectType.PRODUCT, PRODUCT_ID, id, 2)));
            toRelease.forEach(id -> tasks.add(() ->
                inventoryCommandUseCase.release(StockSubjectType.PRODUCT, PRODUCT_ID, id, 2)));

            // when: 확정 10건과 해제 10건을 동시에 실행한다
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 전부 성공하고, total_stock은 확정된 10건(20개)만큼 줄고,
            //       reserved_stock은 확정·해제된 20건(40개) 전부만큼 줄어 0이 된다
            assertThat(outcomes).allMatch(ConcurrentOutcome::success);
            Inventory inventory = findInventory(PRODUCT_ID);
            assertThat(inventory.getTotalStock()).isEqualTo(80);
            assertThat(inventory.getReservedStock()).isZero();
        }

        @Test
        @DisplayName("같은 주문 항목에 확정과 해제가 동시에 도착하면 하나만 성공하고 다른 하나는 충돌로 실패한다.")
        void only_one_succeeds_when_confirm_and_release_race_for_same_order() {
            // given: 재고 10개짜리 상품에 2개를 예약한 주문 항목이 있고,
            //        이 주문 항목에 대해 확정(CONFIRM)과 해제(RELEASE)가 동시에 도착한다.
            //        (payment.completed와 order.item-cancelled가 거의 동시에 발행되는 경합 상황을 재현한다.)
            seedInventory(PRODUCT_ID, 10);
            inventoryCommandUseCase.reserveBulk(
                List.of(new ReserveItemCommand(1L, StockSubjectType.PRODUCT, PRODUCT_ID, 2)));

            List<Runnable> tasks = List.of(
                () -> inventoryCommandUseCase.confirm(StockSubjectType.PRODUCT, PRODUCT_ID, 1L, 2),
                () -> inventoryCommandUseCase.release(StockSubjectType.PRODUCT, PRODUCT_ID, 1L, 2)
            );

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 정확히 하나만 성공하고, 다른 하나는 STOCK_MOVEMENT_CONFLICT로 실패한다.
            //       advisory lock이 둘을 직렬화해, 어느 쪽이 먼저 처리되든 결과는 일관된다.
            assertThat(outcomes).filteredOn(ConcurrentOutcome::success).hasSize(1);
            assertThat(outcomes).filteredOn(o -> !o.success())
                .hasSize(1)
                .allSatisfy(o -> assertThat(o.exception())
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.STOCK_MOVEMENT_CONFLICT));

            // 확정이든 해제든 예약분은 정리되어야 하므로 reservedStock은 0이다.
            assertThat(findInventory(PRODUCT_ID).getReservedStock()).isZero();
        }
    }

    @Nested
    @DisplayName("여러 상품에 대한 요청이 동시에 들어오는 경우 (상품이 겹치는 경우 포함)")
    class MultipleProducts {

        private static final Long PRODUCT_A = 1L;
        private static final Long PRODUCT_B = 2L;

        @Test
        @DisplayName("상품이 겹치지 않는 여러 주문이 동시에 예약되면 모두 성공한다.")
        void succeeds_for_all_when_products_do_not_overlap() {
            // given
            seedInventory(PRODUCT_A, 10);
            seedInventory(PRODUCT_B, 10);
            List<Runnable> tasks = List.of(
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(1L, StockSubjectType.PRODUCT, PRODUCT_A, 3))),
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(2L, StockSubjectType.PRODUCT, PRODUCT_B, 3)))
            );

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then
            assertThat(outcomes).allMatch(ConcurrentOutcome::success);
            assertThat(findInventory(PRODUCT_A).getReservedStock()).isEqualTo(3);
            assertThat(findInventory(PRODUCT_B).getReservedStock()).isEqualTo(3);
        }

        @RepeatedTest(20)
        @DisplayName("상품이 겹치는 두 주문이 반대 순서로 동시에 예약되어도 교착 상태 없이 둘 다 처리된다.")
        void no_deadlock_when_overlapping_products_reserved_in_reverse_order() {
            // given: 상품 A·B 각각 재고 10개. 주문1은 [A, B], 주문2는 [B, A] 순서로 같은 두 상품을 참조한다.
            //        타이밍에 따라 교착상태가 재현될 수 있어 20회 반복한다. (@RepeatedTest(20))
            seedInventory(PRODUCT_A, 10);
            seedInventory(PRODUCT_B, 10);
            long seed = System.nanoTime();
            List<Runnable> tasks = List.of(
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(seed, StockSubjectType.PRODUCT, PRODUCT_A, 1),
                    new ReserveItemCommand(seed + 1, StockSubjectType.PRODUCT, PRODUCT_B, 1))),
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(seed + 2, StockSubjectType.PRODUCT, PRODUCT_B, 1),
                    new ReserveItemCommand(seed + 3, StockSubjectType.PRODUCT, PRODUCT_A, 1)))
            );

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 데드락으로 인한 실패 없이 둘 다 성공해야 한다.
            assertThat(outcomes).allMatch(ConcurrentOutcome::success);
            assertThat(findInventory(PRODUCT_A).getReservedStock()).isEqualTo(2);
            assertThat(findInventory(PRODUCT_B).getReservedStock()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("서로 다른 상품에 대한 동시 요청이 들어오는 경우")
    class DifferentProducts {

        @Test
        @DisplayName("서로 다른 상품에 대한 동시 요청은 서로의 성공 여부에 영향을 주지 않는다.")
        void does_not_affect_other_products_outcome() {
            // given: 상품 A는 재고가 부족(1개)하고, 상품 B는 충분(10개)하다.
            Long productA = 1L;
            Long productB = 2L;
            seedInventory(productA, 1);
            seedInventory(productB, 10);
            List<Runnable> tasks = List.of(
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(1L, StockSubjectType.PRODUCT, productA,
                        5))), // 재고 부족 → 실패 기대
                () -> inventoryCommandUseCase.reserveBulk(List.of(
                    new ReserveItemCommand(2L, StockSubjectType.PRODUCT, productB,
                        5)))  // 재고 충분 → 성공 기대
            );

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 상품 A의 재고 부족이 상품 B의 예약 성공에 영향을 주지 않는다.
            assertThat(outcomes).filteredOn(ConcurrentOutcome::success).hasSize(1);
            assertThat(outcomes).filteredOn(o -> !o.success()).hasSize(1);
            assertThat(findInventory(productB).getReservedStock()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("많은 동시 요청이 들어오는 경우")
    class StressWithLargerConcurrency {

        // 기본 HikariCP 풀 크기를 명시적으로 키운다.
        @DynamicPropertySource
        static void increasePoolSize(DynamicPropertyRegistry registry) {
            registry.add("spring.datasource.hikari.maximum-pool-size", () -> "50");
        }

        @Test
        @DisplayName("동시 요청 100건 중에서도 가용 재고만큼만 정확히 성공하고 과다판매는 발생하지 않는다.")
        void succeeds_exactly_up_to_available_stock_under_heavy_concurrency() {
            // given: 재고 30개짜리 상품에 100개의 서로 다른 주문이 각 1개씩 예약을 시도한다.
            Long productId = 1L;
            seedInventory(productId, 30);
            List<Runnable> tasks = LongStream.rangeClosed(1, 100)
                .mapToObj(i -> (Runnable) () -> inventoryCommandUseCase.reserveBulk(
                    List.of(new ReserveItemCommand(i, StockSubjectType.PRODUCT, productId, 1))))
                .toList();

            // when
            List<ConcurrentOutcome> outcomes = ConcurrencyTestHarness.runSimultaneously(tasks);

            // then: 정확히 30건만 성공하고, 과다판매(reservedStock > 30)는 절대 발생하지 않는다.
            assertThat(outcomes).filteredOn(ConcurrentOutcome::success).hasSize(30);
            assertThat(findInventory(productId).getReservedStock()).isEqualTo(30);
        }
    }
}
