package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetConcernJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.mapper.PetConcernMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.PetConcernJpaRepository;
import com.golajugaenyang.member.domain.entity.PetConcern;
import com.golajugaenyang.member.domain.repository.PetConcernRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetConcernAdapter implements PetConcernRepository {

    private final PetConcernJpaRepository petConcernJpaRepo;

    @Override
    public List<PetConcern> saveAll(List<PetConcern> petConcerns) {
        List<PetConcernJpaEntity> jpaEntities = petConcerns.stream()
            .map(PetConcernMapper::toJpaEntity)
            .toList();

        return petConcernJpaRepo.saveAll(jpaEntities).stream()
            .map(PetConcernMapper::toDomain)
            .toList();
    }

    @Override
    public List<PetConcern> findByPetId(Long petId){
        return petConcernJpaRepo.findByPetId(petId).stream()
                .map(PetConcernMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByPetId(Long petId) {
        petConcernJpaRepo.deleteByPetId(petId);
    }
}
