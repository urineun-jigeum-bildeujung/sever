package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository {
    boolean existsByNickname(String nickname);

    Optional<Member> findByNickname(String nickname);

    Member save(Member member);

    Optional<Member> findByAuthId(Long authId);

    Optional<Member> findByAuthIdIncludingDeleted(Long authId);

    Optional<Member> findById(Long memberId);

    void lockForUpdate(Long memberId);
}
