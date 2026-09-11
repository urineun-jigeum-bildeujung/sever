package com.golajugaenyang.review.out.persistence.entity;

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
@Table(name = "product_feedback_check")
public class ProductFeedbackCheckJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private Long petId;
}
