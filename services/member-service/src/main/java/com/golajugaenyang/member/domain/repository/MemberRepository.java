package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository {
    boolean existsByNickname(String nickname);

    Member save(Member member);

    boolean existsByAuthId(Long authId);

    Optional<Member> findByAuthId(Long authId);

    void lockForUpdate(Long memberId);
}
