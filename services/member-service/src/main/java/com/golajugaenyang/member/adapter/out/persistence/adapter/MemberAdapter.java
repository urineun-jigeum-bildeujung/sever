package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.repository.MemberJpaRepository;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdapter implements MemberRepository {

    private final MemberJpaRepository memberJpaRepo;

    @Override
    public boolean existsByNickname(String nickname){
        return memberJpaRepo.existsByNickname(nickname);
    }

}

