package com.golajugaenyang.member.adapter.out.persistence.mapper;

import com.golajugaenyang.member.adapter.out.persistence.entity.AgreementJpaEntity;
import com.golajugaenyang.member.domain.entity.Agreement;

public class AgreementMapper {

    public static Agreement toDomain(AgreementJpaEntity jpaEntity){
        if(jpaEntity == null) return null;
        return new Agreement(
            jpaEntity.getId(),
            jpaEntity.getAgreementType(),
            jpaEntity.getAgreementVersion(),
            jpaEntity.isAgreed(),
            jpaEntity.getAgreedAt(),
            jpaEntity.getExpiredAt(),
            jpaEntity.getMemberId()
        );
    }

    public static AgreementJpaEntity toJpaEntity(Agreement domain){
        if(domain == null) return null;
        return AgreementJpaEntity.builder()
            .id(domain.getId())
            .agreementType(domain.getAgreementType())
            .agreementVersion(domain.getAgreementVersion())
            .isAgreed(domain.isAgreed())
            .agreedAt(domain.getAgreedAt())
            .expiredAt(domain.getExpiredAt())
            .memberId(domain.getMemberId())
            .build();
    }

}
