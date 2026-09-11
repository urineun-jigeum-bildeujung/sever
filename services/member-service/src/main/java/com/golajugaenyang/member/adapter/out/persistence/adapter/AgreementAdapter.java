package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.entity.AgreementJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.mapper.AgreementMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.AgreementJpaRepository;
import com.golajugaenyang.member.domain.entity.Agreement;
import com.golajugaenyang.member.domain.repository.AgreementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementAdapter implements AgreementRepository {

    private final AgreementJpaRepository agreementJpaRepo;

    @Override
    public List<Agreement> saveAll(List<Agreement> agreements) {
        List<AgreementJpaEntity> jpaEntities = agreements.stream()
            .map(AgreementMapper::toJpaEntity)
            .toList();

        return agreementJpaRepo.saveAll(jpaEntities).stream()
            .map(AgreementMapper::toDomain)
            .toList();
    }

}
