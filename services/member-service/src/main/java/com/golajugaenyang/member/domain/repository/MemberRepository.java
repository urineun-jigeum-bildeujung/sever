package com.golajugaenyang.member.domain.repository;

public interface MemberRepository {
    boolean existsByNickname(String nickname);
}
