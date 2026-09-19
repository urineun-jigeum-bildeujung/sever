package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.entity.PetAllergyJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.mapper.PetAllergyMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.PetAllergyJpaRepository;
import com.golajugaenyang.member.domain.entity.PetAllergy;
import com.golajugaenyang.member.domain.repository.PetAllergyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetAllergyAdapter implements PetAllergyRepository {

    private final PetAllergyJpaRepository petAllergyJpaRepo;

    @Override
    public List<PetAllergy> saveAll(List<PetAllergy> petAllergies) {
        List<PetAllergyJpaEntity> jpaEntities = petAllergies.stream()
            .map(PetAllergyMapper::toJpaEntity)
            .toList();

        return petAllergyJpaRepo.saveAll(jpaEntities).stream()
            .map(PetAllergyMapper::toDomain)
            .toList();
    }

    @Override
    public List<PetAllergy> findByPetId(Long petId) {
        return petAllergyJpaRepo.findByPetId(petId).stream()
                .map(PetAllergyMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByPetId(Long petId) {
        petAllergyJpaRepo.deleteByPetId(petId);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        petAllergyJpaRepo.deleteAllById(ids);
    }
}
