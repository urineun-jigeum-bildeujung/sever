package com.golajugaenyang.review.adapter.out.persistence.adapter;

import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewJpaEntity.reviewJpaEntity;
import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewRecommendJpaEntity.reviewRecommendJpaEntity;

import com.golajugaenyang.review.adapter.out.persistence.entity.QReviewPetSnapshotEmbeddable;
import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.enums.AgeGroup;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
import com.golajugaenyang.review.domain.entity.enums.UsagePeriod;
import com.golajugaenyang.review.domain.repository.ReviewSearchCriteria;
import com.golajugaenyang.review.domain.repository.ReviewSearchRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewSearchRepositoryImpl implements ReviewSearchRepository {

    private static final int PUPPY_MAX_AGE = 1;
    private static final int SENIOR_MIN_AGE = 8;

    private static final int ONE_MONTH_DAYS = 30;
    private static final int THREE_MONTHS_DAYS = 90;
    private static final int SIX_MONTHS_DAYS = 180;
    private static final int ONE_YEAR_DAYS = 365;

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
        if (criteria.ageGroup() != null) {
            petCondition.and(ageGroupCondition(pet, criteria.ageGroup()));
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
        if (criteria.usagePeriod() != null) {
            where.and(usagePeriodCondition(criteria.usagePeriod()));
        }

        // 맞춤보기(내 펫 기준)도 species+breedSize가 같은 한 마리를 가리켜야 하므로
        // 위와 별개의 .any() 참조를 하나 더 둔다.
        if (criteria.personalizedSpecies() != null || criteria.personalizedBreedSize() != null) {
            QReviewPetSnapshotEmbeddable personalizedPet = reviewJpaEntity.pets.any();
            BooleanBuilder personalizedCondition = new BooleanBuilder();
            if (criteria.personalizedSpecies() != null) {
                personalizedCondition.and(personalizedPet.species.eq(criteria.personalizedSpecies()));
            }
            if (criteria.personalizedBreedSize() != null) {
                personalizedCondition.and(personalizedPet.breedSize.eq(criteria.personalizedBreedSize()));
            }
            where.and(personalizedCondition);
        }

        return where;
    }

    private BooleanBuilder ageGroupCondition(QReviewPetSnapshotEmbeddable pet, AgeGroup ageGroup) {
        BooleanBuilder condition = new BooleanBuilder();
        switch (ageGroup) {
            case PUPPY -> condition.and(pet.age.lt(PUPPY_MAX_AGE));
            case SENIOR -> condition.and(pet.age.goe(SENIOR_MIN_AGE));
            case ADULT -> condition.and(pet.age.goe(PUPPY_MAX_AGE))
                    .and(pet.age.lt(SENIOR_MIN_AGE));
        }
        return condition;
    }

    private BooleanBuilder usagePeriodCondition(UsagePeriod usagePeriod) {
        BooleanBuilder condition = new BooleanBuilder();
        switch (usagePeriod) {
            case ONE_MONTH -> condition.and(reviewJpaEntity.usagePeriod.lt(THREE_MONTHS_DAYS));
            case THREE_MONTHS -> condition.and(reviewJpaEntity.usagePeriod.goe(THREE_MONTHS_DAYS))
                    .and(reviewJpaEntity.usagePeriod.lt(SIX_MONTHS_DAYS));
            case SIX_MONTHS -> condition.and(reviewJpaEntity.usagePeriod.goe(SIX_MONTHS_DAYS))
                    .and(reviewJpaEntity.usagePeriod.lt(ONE_YEAR_DAYS));
            case ONE_YEAR -> condition.and(reviewJpaEntity.usagePeriod.goe(ONE_YEAR_DAYS));
        }
        return condition;
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
