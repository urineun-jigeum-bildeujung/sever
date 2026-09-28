package com.golajugaenyang.review.adapter.out.persistence.adapter;

import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewJpaEntity.reviewJpaEntity;
import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewRecommendJpaEntity.reviewRecommendJpaEntity;

import com.golajugaenyang.review.adapter.out.persistence.entity.QReviewPetSnapshotEmbeddable;
import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
import com.golajugaenyang.review.domain.repository.ProductRatingSummary;
import com.golajugaenyang.review.domain.repository.ReviewSearchCriteria;
import com.golajugaenyang.review.domain.repository.ReviewSearchRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewSearchRepositoryImpl implements ReviewSearchRepository {

    private static final int AGE_TOLERANCE_YEARS = 2;
    private static final double WEIGHT_TOLERANCE_RATIO = 0.2;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> search(ReviewSearchCriteria criteria) {
        List<ReviewJpaEntity> results = queryFactory
                .selectFrom(reviewJpaEntity)
                .where(baseWhere(criteria))
                .orderBy(orderSpecifier(criteria.sort()))
                .offset((long) criteria.page() * criteria.size())
                .limit(criteria.size())
                .fetch();
        return results.stream().map(ReviewMapper::toDomain).toList();
    }

    @Override
    public long count(ReviewSearchCriteria criteria) {
        Long total = queryFactory
                .select(reviewJpaEntity.count())
                .from(reviewJpaEntity)
                .where(baseWhere(criteria))
                .fetchOne();
        return total != null ? total : 0L;
    }

    @Override
    public Double averageRating(Long productId) {
        return queryFactory
                .select(reviewJpaEntity.starRate.avg())
                .from(reviewJpaEntity)
                .where(reviewJpaEntity.productId.eq(productId))
                .fetchOne();
    }

    @Override
    public List<ProductRatingSummary> findRatingSummaries(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .select(Projections.constructor(ProductRatingSummary.class,
                        reviewJpaEntity.productId,
                        reviewJpaEntity.starRate.avg(),
                        reviewJpaEntity.count()))
                .from(reviewJpaEntity)
                .where(reviewJpaEntity.productId.in(productIds))
                .groupBy(reviewJpaEntity.productId)
                .fetch();
    }

    private BooleanBuilder baseWhere(ReviewSearchCriteria criteria) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(reviewJpaEntity.productId.eq(criteria.productId()));

        // 아래 필터(species~weightMax)는 전부 "리뷰에 딸린 펫들 중 같은 한 마리"가
        // 전부 만족해야 함 - .any()를 필터 개수만큼 따로 부르면 QueryDSL이 그때마다
        // 별도 EXISTS 서브쿼리를 만들어서, 조건마다 다른 펫이 걸려도 매칭돼버림
        // (예: 대형견 1마리 + 5kg 고양이 1마리인 리뷰가 "대형견 AND 5kg 이하"에 걸림).
        // 그래서 같은 QReviewPetSnapshotEmbeddable 참조 하나를 재사용해서 한 EXISTS
        // 안에 모든 조건을 몰아넣는다.
        QReviewPetSnapshotEmbeddable pet = reviewJpaEntity.pets.any();
        BooleanBuilder petCondition = new BooleanBuilder();
        boolean hasPetCondition = false;

        if (criteria.species() != null) {
            petCondition.and(pet.species.eq(criteria.species()));
            hasPetCondition = true;
        }
        if (criteria.breedId() != null) {
            petCondition.and(pet.breedId.eq(criteria.breedId()));
            hasPetCondition = true;
        }
        if (criteria.ageMin() != null) {
            petCondition.and(pet.age.goe(criteria.ageMin()));
            hasPetCondition = true;
        }
        if (criteria.ageMax() != null) {
            petCondition.and(pet.age.loe(criteria.ageMax()));
            hasPetCondition = true;
        }
        if (criteria.neutered() != null) {
            petCondition.and(pet.neutered.eq(criteria.neutered()));
            hasPetCondition = true;
        }
        if (criteria.weightMin() != null) {
            petCondition.and(pet.weight.goe(criteria.weightMin()));
            hasPetCondition = true;
        }
        if (criteria.weightMax() != null) {
            petCondition.and(pet.weight.loe(criteria.weightMax()));
            hasPetCondition = true;
        }
        if (hasPetCondition) {
            where.and(petCondition);
        }

        if (criteria.healthConcerns() != null && !criteria.healthConcerns().isEmpty()) {
            where.and(reviewJpaEntity.petHealthConcernCodes.any().in(criteria.healthConcerns()));
        }
        if (criteria.usagePeriodMinDays() != null) {
            where.and(reviewJpaEntity.usagePeriod.goe(criteria.usagePeriodMinDays()));
        }
        if (criteria.usagePeriodMaxDays() != null) {
            where.and(reviewJpaEntity.usagePeriod.loe(criteria.usagePeriodMaxDays()));
        }

        // 맞춤보기(내 펫 기준)도 species+age+weight+neutered가 같은 한 마리를 가리켜야 하므로
        // 위와 별개의 .any() 참조를 하나 더 둔다.
        if (criteria.personalizedSpecies() != null || criteria.personalizedAge() != null
                || criteria.personalizedWeight() != null || criteria.personalizedNeutered() != null) {
            QReviewPetSnapshotEmbeddable personalizedPet = reviewJpaEntity.pets.any();
            BooleanBuilder personalizedCondition = new BooleanBuilder();
            if (criteria.personalizedSpecies() != null) {
                personalizedCondition.and(personalizedPet.species.eq(criteria.personalizedSpecies()));
            }
            if (criteria.personalizedAge() != null) {
                personalizedCondition.and(personalizedPet.age.goe(criteria.personalizedAge() - AGE_TOLERANCE_YEARS))
                        .and(personalizedPet.age.loe(criteria.personalizedAge() + AGE_TOLERANCE_YEARS));
            }
            if (criteria.personalizedWeight() != null) {
                double min = criteria.personalizedWeight() * (1 - WEIGHT_TOLERANCE_RATIO);
                double max = criteria.personalizedWeight() * (1 + WEIGHT_TOLERANCE_RATIO);
                personalizedCondition.and(personalizedPet.weight.goe(min)).and(personalizedPet.weight.loe(max));
            }
            if (criteria.personalizedNeutered() != null) {
                personalizedCondition.and(personalizedPet.neutered.eq(criteria.personalizedNeutered()));
            }
            where.and(personalizedCondition);
        }

        // 건강 관심정보는 펫 단위가 아니라 리뷰 단위 컬렉션이라, 대상 펫의 건강 관심정보 중
        // 하나라도 겹치면 매칭으로 본다 (기존 healthConcerns 필터와 동일한 방식).
        if (criteria.personalizedHealthConcerns() != null && !criteria.personalizedHealthConcerns().isEmpty()) {
            where.and(reviewJpaEntity.petHealthConcernCodes.any().in(criteria.personalizedHealthConcerns()));
        }

        return where;
    }

    private OrderSpecifier<?> orderSpecifier(ReviewSortType sort) {
        if (sort == null) {
            return reviewJpaEntity.createdAt.desc();
        }
        return switch (sort) {
            case LATEST -> reviewJpaEntity.createdAt.desc();
            case RATING_HIGH -> reviewJpaEntity.starRate.desc();
            case RATING_LOW -> reviewJpaEntity.starRate.asc();
            case RECOMMEND -> {
                var likeCountSubquery = JPAExpressions.select(reviewRecommendJpaEntity.count())
                        .from(reviewRecommendJpaEntity)
                        .where(reviewRecommendJpaEntity.reviewId.eq(reviewJpaEntity.id));
                yield new OrderSpecifier<>(Order.DESC, likeCountSubquery);
            }
        };
    }
}
