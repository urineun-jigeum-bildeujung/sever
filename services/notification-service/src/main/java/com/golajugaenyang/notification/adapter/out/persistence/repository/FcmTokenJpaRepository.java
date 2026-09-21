package com.golajugaenyang.notification.adapter.out.persistence.repository;

import com.golajugaenyang.notification.adapter.out.persistence.entity.FcmTokenJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenJpaEntity, Long> {

    Optional<FcmTokenJpaEntity> findByToken(String token);

    List<FcmTokenJpaEntity> findByMemberIdIn(List<Long> memberIds);

    @Query("select distinct f.memberId from FcmTokenJpaEntity f")
    List<Long> findDistinctMemberIds();
}
