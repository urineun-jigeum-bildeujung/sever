package com.golajugaenyang.review.adapter.out.persistence.adapter;

import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewJpaEntity.reviewJpaEntity;
import static com.golajugaenyang.review.adapter.out.persistence.entity.QReviewRecommendJpaEntity.reviewRecommendJpaEntity;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewMapper;
import com.golajugaenyang.review.domain.entity.Review;
import com.golajugaenyang.review.domain.entity.enums.AgeGroup;
import com.golajugaenyang.review.domain.entity.enums.ReviewSortType;
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

        if (criteria.species() != null) {
            where.and(reviewJpaEntity.petSpecies.eq(criteria.species()));
        }
        if (criteria.breedId() != null) {
            where.and(reviewJpaEntity.petBreedId.eq(criteria.breedId()));
        }
        if (criteria.ageGroup() != null) {
            where.and(ageGroupCondition(criteria.ageGroup()));
        }
        if (criteria.neutered() != null) {
            where.and(reviewJpaEntity.petNeutered.eq(criteria.neutered()));
        }
        if (criteria.weightMin() != null) {
            where.and(reviewJpaEntity.petWeight.goe(criteria.weightMin()));
        }
        if (criteria.weightMax() != null) {
            where.and(reviewJpaEntity.petWeight.loe(criteria.weightMax()));
        }
        if (criteria.healthConcerns() != null && !criteria.healthConcerns().isEmpty()) {
            where.and(reviewJpaEntity.petHealthConcernCodes.any().in(criteria.healthConcerns()));
        }
        if (criteria.usagePeriod() != null) {
            where.and(reviewJpaEntity.usagePeriod.eq(criteria.usagePeriod()));
        }
        if (criteria.personalizedSpecies() != null) {
            where.and(reviewJpaEntity.petSpecies.eq(criteria.personalizedSpecies()));
        }
        if (criteria.personalizedBreedSize() != null) {
            where.and(reviewJpaEntity.petBreedSize.eq(criteria.personalizedBreedSize()));
        }
        return where;
    }

    private BooleanBuilder ageGroupCondition(AgeGroup ageGroup) {
        BooleanBuilder condition = new BooleanBuilder();
        switch (ageGroup) {
            case PUPPY -> condition.and(reviewJpaEntity.petAge.lt(PUPPY_MAX_AGE));
            case SENIOR -> condition.and(reviewJpaEntity.petAge.goe(SENIOR_MIN_AGE));
            case ADULT -> condition.and(reviewJpaEntity.petAge.goe(PUPPY_MAX_AGE))
                    .and(reviewJpaEntity.petAge.lt(SENIOR_MIN_AGE));
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
