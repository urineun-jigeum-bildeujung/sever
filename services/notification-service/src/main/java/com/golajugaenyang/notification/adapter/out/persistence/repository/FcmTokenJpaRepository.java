package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.FcmTokenJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenJpaEntity, Long> {

    Optional<FcmTokenJpaEntity> findByToken(String token);

    List<FcmTokenJpaEntity> findByMemberIdIn(List<Long> memberIds);

    @Query("select distinct f.memberId from FcmTokenJpaEntity f")
    List<Long> findDistinctMemberIds();

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO fcm_token (member_id, token, created_at, updated_at)
        VALUES (:memberId, :token, now(), now())
        ON CONFLICT ON CONSTRAINT uk_fcm_token_token DO UPDATE
        SET member_id = EXCLUDED.member_id, updated_at = now()
        """, nativeQuery = true)
    void upsert(@Param("memberId") Long memberId, @Param("token") String token);

    @Transactional
    @Modifying
    void deleteByToken(String token);
}
