package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.out.persistence.mapper.ConcernMasterMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.ConcernMasterJpaRepository;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import com.golajugaenyang.member.domain.repository.ConcernMasterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcernMasterAdapter implements ConcernMasterRepository {

    private final ConcernMasterJpaRepository concernMasterJpaRepo;

    @Override
    public List<ConcernMaster> findByConcernCodeIn(List<String> concernCodes) {
        return concernMasterJpaRepo.findByConcernCodeIn(concernCodes).stream()
            .map(ConcernMasterMapper::toDomain)
            .toList();
    }

    @Override
    public List<ConcernMaster> findBySpecies(Species species) {
        return concernMasterJpaRepo.findBySpecies(species).stream()
            .map(ConcernMasterMapper::toDomain)
            .toList();
    }

    @Override
    public List<ConcernMaster> findByIdIn(List<Long> ids) {
        return concernMasterJpaRepo.findAllById(ids).stream()
            .map(ConcernMasterMapper::toDomain)
            .toList();
    }
}
