package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Member;

public interface MemberRepository {
    boolean existsByNickname(String nickname);

    Member save(Member member);

    boolean existsByAuthId(Long authId);
}
