package com.golajugaenyang.review.adapter.out.persistence.mapper;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewPetSnapshotEmbeddable;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.ReviewPetSnapshot;
import java.util.List;

public class ReviewMapper {

    public static Review toDomain(ReviewJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        List<ReviewPetSnapshot> pets = jpaEntity.getPets().stream()
                .map(ReviewMapper::toDomainPet)
                .toList();

        return new Review(
                jpaEntity.getId(),
                jpaEntity.getText(),
                jpaEntity.getStarRate(),
                jpaEntity.getUsagePeriod(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt(),
                jpaEntity.getDeletedAt(),
                jpaEntity.getMemberId(),
                jpaEntity.getProductId(),
                jpaEntity.getDataOrigin(),
                jpaEntity.isSynthetic(),
                jpaEntity.getDatasetRunId(),
                pets,
                jpaEntity.getPetHealthConcernCodes()
        );
    }

    public static ReviewJpaEntity toJpaEntity(Review domain) {
        if (domain == null) return null;

        List<ReviewPetSnapshotEmbeddable> pets = domain.getPets().stream()
                .map(ReviewMapper::toEmbeddablePet)
                .toList();

        return ReviewJpaEntity.builder()
                .id(domain.getId())
                .text(domain.getText())
                .starRate(domain.getStarRate())
                .usagePeriod(domain.getUsagePeriod())
                .deletedAt(domain.getDeletedAt())
                .memberId(domain.getMemberId())
                .productId(domain.getProductId())
                .dataOrigin(domain.getDataOrigin())
                .isSynthetic(domain.isSynthetic())
                .datasetRunId(domain.getDatasetRunId())
                .pets(pets)
                .petHealthConcernCodes(domain.getPetHealthConcernCodes())
                .build();
    }

    private static ReviewPetSnapshot toDomainPet(ReviewPetSnapshotEmbeddable embeddable) {
        return new ReviewPetSnapshot(
                embeddable.getPetId(),
                embeddable.getName(),
                embeddable.getSpecies(),
                embeddable.getBreedId(),
                embeddable.getAge(),
                embeddable.getSex(),
                embeddable.isNeutered(),
                embeddable.getBreedSize(),
                embeddable.getWeight()
        );
    }

    private static ReviewPetSnapshotEmbeddable toEmbeddablePet(ReviewPetSnapshot pet) {
        return new ReviewPetSnapshotEmbeddable(
                pet.getPetId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreedId(),
                pet.getAge(),
                pet.getSex(),
                pet.isNeutered(),
                pet.getBreedSize(),
                pet.getWeight()
        );
    }
}
