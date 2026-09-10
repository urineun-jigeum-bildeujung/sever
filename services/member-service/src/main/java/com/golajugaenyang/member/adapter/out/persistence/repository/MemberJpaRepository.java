package com.golajugaenyang.member.adapter.out.persistence.repository;

import com.golajugaenyang.member.adapter.out.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    boolean existsByNickname(String nickname);
}
