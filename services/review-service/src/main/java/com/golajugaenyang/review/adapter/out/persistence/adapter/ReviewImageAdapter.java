package com.golajugaenyang.review.adapter.out.persistence.adapter;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewImageJpaEntity;
import com.golajugaenyang.review.adapter.out.persistence.mapper.ReviewImageMapper;
import com.golajugaenyang.review.adapter.out.persistence.repository.ReviewImageJpaRepository;
import com.golajugaenyang.review.domain.entity.ReviewImage;
import com.golajugaenyang.review.domain.repository.ReviewImageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewImageAdapter implements ReviewImageRepository {

    private final ReviewImageJpaRepository reviewImageJpaRepo;

    @Override
    public List<ReviewImage> saveAll(List<ReviewImage> images) {
        List<ReviewImageJpaEntity> jpaEntities = images.stream()
                .map(ReviewImageMapper::toJpaEntity)
                .toList();
        return reviewImageJpaRepo.saveAll(jpaEntities).stream()
                .map(ReviewImageMapper::toDomain)
                .toList();
    }

    @Override
    public List<ReviewImage> findRepresentativeImagesByReviewIds(List<Long> reviewIds) {
        return reviewImageJpaRepo.findByReviewIdInAndSortOrder(reviewIds, 0).stream()
                .map(ReviewImageMapper::toDomain)
                .toList();
    }

    @Override
    public List<ReviewImage> findByProductId(Long productId, int page, int size) {
        return reviewImageJpaRepo.findByProductId(productId, PageRequest.of(page, size)).stream()
                .map(ReviewImageMapper::toDomain)
                .toList();
    }

    @Override
    public long countByProductId(Long productId) {
        return reviewImageJpaRepo.countByProductId(productId);
    }
}
