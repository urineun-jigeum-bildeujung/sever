package com.golajugaenyang.review.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.review.domain.entity.enums.DataOrigin;
import com.golajugaenyang.review.domain.entity.enums.Sex;
import com.golajugaenyang.review.domain.entity.enums.UsagePeriod;
import java.time.Instant;
import java.util.Set;
import lombok.Getter;

@Getter
public class Review {

    private Long id;
    private String text;
    private double starRate;
    private UsagePeriod usagePeriod;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long memberId;
    private Long productId;
    private Long petId;
    private DataOrigin dataOrigin;
    private boolean isSynthetic;
    private Long datasetRunId;

    // 펫 정보는 시간이 지나면 바뀔 수 있어서(나이, 중성화 여부 등), 작성 시점 상태를 그대로 고정해서 저장한다.
    private String petName;
    private Species petSpecies;
    private Long petBreedId;
    private int petAge;
    private Sex petSex;
    private boolean petNeutered;
    private TargetBreedSize petBreedSize;
    private double petWeight;
    private Set<String> petHealthConcernCodes;

    public Review(Long id, String text, double starRate, UsagePeriod usagePeriod,
                  Instant createdAt, Instant updatedAt, Instant deletedAt, Long memberId,
                  Long productId, Long petId, DataOrigin dataOrigin, boolean isSynthetic, Long datasetRunId,
                  String petName, Species petSpecies, Long petBreedId, int petAge, Sex petSex,
                  boolean petNeutered, TargetBreedSize petBreedSize, double petWeight,
                  Set<String> petHealthConcernCodes) {
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
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.petBreedId = petBreedId;
        this.petAge = petAge;
        this.petSex = petSex;
        this.petNeutered = petNeutered;
        this.petBreedSize = petBreedSize;
        this.petWeight = petWeight;
        this.petHealthConcernCodes = petHealthConcernCodes;
    }
}
