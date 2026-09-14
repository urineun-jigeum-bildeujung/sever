package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Pet;
import java.util.List;

public interface PetRepository {

    Pet save(Pet pet);

    boolean existsByMemberId(Long memberId);

    List<Pet> findByMemberId(Long memberId);
}
