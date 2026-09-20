package com.golajugaenyang.review.adapter.out.persistence.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.jpa.entity.BaseTimeEntity;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import com.golajugaenyang.review.domain.entity.enums.Sex;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Long petId;

    @Enumerated(EnumType.STRING)
    private DataOrigin dataOrigin;

    @Column(nullable = false)
    private boolean isSynthetic;

    private Long datasetRunId;

    @Column(nullable = false)
    private String petName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species petSpecies;

    @Column(nullable = false)
    private Long petBreedId;

    @Column(nullable = false)
    private int petAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex petSex;

    @Column(nullable = false)
    private boolean petNeutered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetBreedSize petBreedSize;

    @Column(nullable = false)
    private double petWeight;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_health_concern", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "concern_code")
    @Builder.Default
    private Set<String> petHealthConcernCodes = Set.of();
}
