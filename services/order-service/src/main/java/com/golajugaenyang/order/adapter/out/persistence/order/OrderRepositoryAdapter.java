package com.golajugaenyang.order.adapter.out.persistence.order;


import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.application.order.port.out.dto.PurchaseRecord;
import com.golajugaenyang.order.domain.order.Order;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.BASIC_ISO_DATE;

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final EntityManager entityManager;

    @Override
    public boolean existsByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey) {
        return orderJpaRepository.existsByMemberIdAndIdempotencyKey(memberId, idempotencyKey);
    }

    @Override
    public Order findByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey) {
        return orderJpaRepository.findByMemberIdAndIdempotencyKey(memberId, idempotencyKey);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException(
                "대상 주문을 찾을 수 없습니다. orderId=" + id));
    }

    @Override
    public Order findByIdOrNull(Long id) {
        return orderJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Order findByOrderItemIdOrNull(Long orderItemId) {
        return orderItemJpaRepository.findOrderByOrderItemId(orderItemId).orElse(null);
    }

    @Override
    public List<PurchaseRecord> findPurchases(Long memberId, Long productId) {
        return orderItemJpaRepository.findPurchases(memberId, productId).stream()
            .map(p -> new PurchaseRecord(
                p.orderId(), p.orderItemId(), p.orderStatus().name(),
                p.confirmedAt(), p.itemStatus().name(), p.orderedAt()))
            .toList();
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public String generateOrderNumber() {
        long seq = ((Number) entityManager
            .createNativeQuery("select nextval('order_number_seq')")
            .getSingleResult()).longValue();
        String datePart = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DATE_FORMAT);
        return "ORD-%s-%06d".formatted(datePart, seq);
    }
}
