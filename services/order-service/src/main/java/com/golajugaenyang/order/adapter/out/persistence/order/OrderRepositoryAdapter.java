package com.golajugaenyang.order.adapter.out.persistence.order;


import com.golajugaenyang.order.application.order.port.out.OrderRepositoryPort;
import com.golajugaenyang.order.domain.order.Order;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.BASIC_ISO_DATE;

    private final OrderJpaRepository orderJpaRepository;
    private final EntityManager entityManager;

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return orderJpaRepository.existsByIdempotencyKey(idempotencyKey);
    }

    @Override
    public Order findByIdempotencyKey(String idempotencyKey) {
        return orderJpaRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException(
                "대상 주문을 찾을 수 없습니다. orderId=" + id));
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
