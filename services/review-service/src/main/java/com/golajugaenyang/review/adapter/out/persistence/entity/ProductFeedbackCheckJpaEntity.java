package com.golajugaenyang.review.adapter.out.persistence.entity;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckAnswer;
import com.golajugaenyang.review.domain.entity.enums.FeedbackCheckStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_feedback_check",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_feedback_check_order_product",
                columnNames = "order_product_id")
)
public class ProductFeedbackCheckJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCheckStatus feedbackCheckStatus;

    @Enumerated(EnumType.STRING)
    private FeedbackCheckAnswer feedbackCheckAnswer;

    private Instant checkAvailableAt;

    private Instant checkExpiresAt;

    private Instant answeredAt;

    @Column(nullable = false)
    private Long orderProductId;

    /**
     * 주문 시점엔 있지만(OrderItem.petId), order-service의 confirmed-items 응답에
     * 아직 petId가 노출되지 않아 당분간 null로 둔다. 추후 필드 추가되면 채워 넣을 예정.
     */
    private Long petId;
}
