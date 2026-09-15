package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.mapper.PetMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.PetJpaRepository;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.repository.PetRepository;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetAdapter implements PetRepository {

    private static final String DEFAULT_PET_UNIQUE_CONSTRAINT = "uk_pet_member_default";

    private final PetJpaRepository petJpaRepo;

    @Override
    public Pet save(Pet pet){
        try {
            return PetMapper.toDomain(petJpaRepo.save(PetMapper.toJpaEntity(pet)));
        } catch (DataIntegrityViolationException e) {
            if (isDefaultPetConflict(e)) {
                return PetMapper.toDomain(petJpaRepo.save(PetMapper.toJpaEntity(pet.withIsDefault(false))));
            }
            throw e;
        }
    }

    @Override
    public boolean existsByMemberId(Long memberId){
        return petJpaRepo.existsByMemberId(memberId);
    }

    @Override
    public List<Pet> findByMemberId(Long memberId) {
        return petJpaRepo.findByMemberId(memberId).stream()
            .map(PetMapper::toDomain)
            .toList();
    }

    private boolean isDefaultPetConflict(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
            && DEFAULT_PET_UNIQUE_CONSTRAINT.equals(cve.getConstraintName());
    }

    @Override
    public Optional<Pet> findById(Long petId){
        return petJpaRepo.findById(petId)
                .map(PetMapper::toDomain);
    }
}
