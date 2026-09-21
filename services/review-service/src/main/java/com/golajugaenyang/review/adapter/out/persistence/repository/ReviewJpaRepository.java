package com.golajugaenyang.review.adapter.out.persistence.repository;

import com.golajugaenyang.review.adapter.out.persistence.entity.ReviewJpaEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    @Query("""
        select r.id from ReviewJpaEntity r
        where r.productId = :productId
        and exists (select 1 from ReviewImageJpaEntity ri where ri.reviewId = r.id)
        order by r.createdAt desc
        """)
    List<Long> findRecentReviewIdsWithImageByProductId(@Param("productId") Long productId, Pageable pageable);

    List<ReviewJpaEntity> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @Query("select r.productId from ReviewJpaEntity r where r.memberId = :memberId and r.productId in :productIds")
    List<Long> findReviewedProductIds(@Param("memberId") Long memberId, @Param("productIds") List<Long> productIds);
}
