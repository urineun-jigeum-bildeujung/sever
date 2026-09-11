package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.mapper.MemberMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.MemberJpaRepository;
import com.golajugaenyang.member.domain.entity.Member;
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

    @Override
    public Member save(Member member) {
        return MemberMapper.toDomain(memberJpaRepo.save(MemberMapper.toJpaEntity(member)));
    }

    @Override
    public boolean existsByAuthId(Long authId){
        return memberJpaRepo.existsByAuthId(authId);
    }

}

