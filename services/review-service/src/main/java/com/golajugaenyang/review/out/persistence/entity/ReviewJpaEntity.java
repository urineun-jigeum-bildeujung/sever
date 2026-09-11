package com.golajugaenyang.review.out.persistence.entity;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class ReviewJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(nullable = false)
    private double starRate;

    @Column(nullable = false)
    private int usagePeriod;

    private Instant deletedAt;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long petId;

    @Enumerated(EnumType.STRING)
    private DataOrigin dataOrigin;

    @Column(nullable = false)
    private boolean isSynthetic;

    private Long datasetRunId;
}
