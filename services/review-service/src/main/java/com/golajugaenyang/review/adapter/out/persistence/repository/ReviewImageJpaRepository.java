package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewImageJpaEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewImageJpaRepository extends JpaRepository<ReviewImageJpaEntity, Long> {

    List<ReviewImageJpaEntity> findByReviewIdInAndSortOrder(List<Long> reviewIds, int sortOrder);

    @Query("""
        select ri from ReviewImageJpaEntity ri, ReviewJpaEntity r
        where ri.reviewId = r.id and r.productId = :productId
        order by r.createdAt desc, ri.sortOrder asc
        """)
    List<ReviewImageJpaEntity> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("""
        select count(ri) from ReviewImageJpaEntity ri, ReviewJpaEntity r
        where ri.reviewId = r.id and r.productId = :productId
        """)
    long countByProductId(@Param("productId") Long productId);
}
