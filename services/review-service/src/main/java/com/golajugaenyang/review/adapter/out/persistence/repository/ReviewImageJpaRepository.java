package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewImageJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageJpaRepository extends JpaRepository<ReviewImageJpaEntity, Long> {

    List<ReviewImageJpaEntity> findByReviewIdInAndSortOrder(List<Long> reviewIds, int sortOrder);
}
