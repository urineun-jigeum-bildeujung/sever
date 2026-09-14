package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.out.persistence.mapper.BreedMasterMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.BreedMasterJpaRepository;
import com.golajugaenyang.member.domain.entity.BreedMaster;
import com.golajugaenyang.member.domain.repository.BreedMasterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BreedMasterAdapter implements BreedMasterRepository {

    private final BreedMasterJpaRepository breedMasterJpaRepo;

    @Override
    public List<BreedMaster> findBySpecies(Species species) {
        return breedMasterJpaRepo.findBySpecies(species).stream()
            .map(BreedMasterMapper::toDomain)
            .toList();
    }
}
