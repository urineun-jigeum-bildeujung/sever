package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class Review {

    private Long id;
    private String text;
    private double starRate;
    private int usagePeriod;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long memberId;
    private Long productId;
    private DataOrigin dataOrigin;
    private boolean isSynthetic;
    private Long datasetRunId;
    private List<ReviewPetSnapshot> pets;
    private Set<String> petHealthConcernCodes;

    public Review(Long id, String text, double starRate, int usagePeriod,
                  Instant createdAt, Instant updatedAt, Instant deletedAt, Long memberId,
                  Long productId, DataOrigin dataOrigin, boolean isSynthetic, Long datasetRunId,
                  List<ReviewPetSnapshot> pets, Set<String> petHealthConcernCodes) {
        this.id = id;
        this.text = text;
        this.starRate = starRate;
        this.usagePeriod = usagePeriod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.memberId = memberId;
        this.productId = productId;
        this.dataOrigin = dataOrigin;
        this.isSynthetic = isSynthetic;
        this.datasetRunId = datasetRunId;
        this.pets = pets;
        this.petHealthConcernCodes = petHealthConcernCodes;
    }
}
