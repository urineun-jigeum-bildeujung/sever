package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import lombok.Getter;

import java.time.Instant;

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
    private Long petId;
    private DataOrigin dataOrigin;
    private boolean isSynthetic;
    private Long datasetRunId;

    public Review(Long id, String text, double starRate, int usagePeriod,
                  Instant createdAt, Instant updatedAt, Instant deletedAt, Long memberId,
                  Long productId, Long petId, DataOrigin dataOrigin, boolean isSynthetic, Long datasetRunId) {
        this.id = id;
        this.text = text;
        this.starRate = starRate;
        this.usagePeriod = usagePeriod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.memberId = memberId;
        this.productId = productId;
        this.petId = petId;
        this.dataOrigin = dataOrigin;
        this.isSynthetic = isSynthetic;
        this.datasetRunId = datasetRunId;
    }

}
