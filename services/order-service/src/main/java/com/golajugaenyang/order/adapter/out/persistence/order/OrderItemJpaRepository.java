package com.golajugaenyang.order.adapter.out.persistence.order;

import com.golajugaenyang.order.adapter.out.persistence.order.dto.ConfirmedPurchaseItemProjection;
import com.golajugaenyang.order.adapter.out.persistence.order.dto.PurchaseVerificationProjection;
import com.golajugaenyang.order.domain.order.Order;
import com.golajugaenyang.order.domain.order.OrderItem;
import com.golajugaenyang.order.domain.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        select new com.golajugaenyang.order.adapter.out.persistence.order.dto.PurchaseVerificationProjection(
            o.id, oi.id, o.orderStatus, o.confirmedAt, oi.itemStatus, o.orderedAt)
        from OrderItem oi join oi.order o
        where o.memberId = :memberId and oi.productId = :productId
        order by o.orderedAt desc
        """)
    List<PurchaseVerificationProjection> findPurchases(
        @Param("memberId") Long memberId, @Param("productId") Long productId);

    @Modifying
    @Query("""
        update OrderItem oi set oi.activeClaimStatus = :claimStatus
        where oi.id = :orderItemId and oi.activeClaimStatus is null
        """)
    int claimForNewRequest(
        @Param("orderItemId") Long orderItemId,
        @Param("claimStatus") String claimStatus);

    @Query("select oi.order from OrderItem oi where oi.id = :orderItemId")
    Optional<Order> findOrderByOrderItemId(@Param("orderItemId") Long orderItemId);

    @Query("""
        select new com.golajugaenyang.order.adapter.out.persistence.order.dto.ConfirmedPurchaseItemProjection(
            oi.productId, oi.petId, o.id, oi.id, o.orderStatus, oi.itemStatus, o.confirmedAt)
        from OrderItem oi join oi.order o
        where o.memberId = :memberId and o.orderStatus = :orderStatus
        order by o.confirmedAt desc, oi.id desc
        """)
    List<ConfirmedPurchaseItemProjection> findConfirmedPurchaseItems(
        @Param("memberId") Long memberId,
        @Param("orderStatus") OrderStatus orderStatus,
        Pageable pageable);
}
