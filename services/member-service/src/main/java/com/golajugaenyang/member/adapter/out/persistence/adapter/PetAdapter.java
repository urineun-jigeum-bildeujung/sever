package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.mapper.PetMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.PetJpaRepository;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetAdapter implements PetRepository {

    private final PetJpaRepository petJpaRepo;

    @Override
    public Pet save(Pet pet){
        return PetMapper.toDomain(petJpaRepo.save(PetMapper.toJpaEntity(pet)));
    }
}
