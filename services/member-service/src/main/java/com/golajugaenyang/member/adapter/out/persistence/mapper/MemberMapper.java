package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.MemberJpaEntity;
import com.golajugaenyang.member.domain.entity.Member;

public class MemberMapper {

    public static Member toDomain(MemberJpaEntity jpaEntity){
        if(jpaEntity == null ) return  null;
        return new Member(
            jpaEntity.getId(),
            jpaEntity.getNickname(),
            jpaEntity.getProfileImage(),
            jpaEntity.getName(),
            jpaEntity.getBirth(),
            jpaEntity.getPhone(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt(),
            jpaEntity.getAuthId()
        );
    }

    public static MemberJpaEntity toJpaEntity(Member domain){
        if(domain == null) return null;
        return MemberJpaEntity.builder()
            .id(domain.getId())
            .nickname(domain.getNickname())
            .profileImage(domain.getProfileImage())
            .name(domain.getName())
            .birth(domain.getBirth())
            .phone(domain.getPhone())
            .authId(domain.getAuthId())
            .build();
    }

}
