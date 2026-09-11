package com.golajugaenyang.review.out.persistence.entity;

import com.golajugaenyang.review.domain.entity.enums.ReviewAnswer;
import com.golajugaenyang.review.domain.entity.enums.ReviewQuestionType;
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
@Table(name = "review_question")
public class ReviewQuestionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewQuestionType reviewQuestionType;

    @Enumerated(EnumType.STRING)
    private ReviewAnswer reviewAnswer;

    private Instant updatedAt;

    @Column(nullable = false)
    private Long reviewId;
}
