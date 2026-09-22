package com.golajugaenyang.review.adapter.out.persistence.entity;

import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review", uniqueConstraints = {
    @UniqueConstraint(name = "uk_review_member_product", columnNames = {"member_id", "product_id"})
})
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

    @Enumerated(EnumType.STRING)
    private DataOrigin dataOrigin;

    @Column(nullable = false)
    private boolean isSynthetic;

    private Long datasetRunId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_pet", joinColumns = @JoinColumn(name = "review_id"))
    @Builder.Default
    private List<ReviewPetSnapshotEmbeddable> pets = List.of();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_health_concern", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "concern_code")
    @Builder.Default
    private Set<String> petHealthConcernCodes = Set.of();
}
