package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetRegisterRequest;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.entity.PetAllergy;
import com.golajugaenyang.member.domain.entity.PetConcern;
import com.golajugaenyang.member.domain.repository.ConcernMasterRepository;
import com.golajugaenyang.member.domain.repository.PetAllergyRepository;
import com.golajugaenyang.member.domain.repository.PetConcernRepository;
import com.golajugaenyang.member.domain.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepo;
    private final PetConcernRepository petConcernRepo;
    private final PetAllergyRepository petAllergyRepo;
    private final ConcernMasterRepository concernMasterRepo;

    @Transactional
    public Long registerPet(Long memberId, PetRegisterRequest request) {
        Pet savedPet = petRepo.save(
                new Pet(null, false, request.name(), request.sex(), request.isNeutered(),
                        request.species(), request.age(), request.birthDate(), request.size(),
                        request.weight(), request.bcs(), request.image(), null,
                        memberId, request.breedId(), null, null)
        );

        List<String> concernCodes = request.healthConcerns() == null ? List.of() : request.healthConcerns();

        if(!concernCodes.isEmpty()){
            List<ConcernMaster> concernMasters = concernMasterRepo.findByConcernCodeIn(concernCodes);

            List<PetConcern> petConcerns = concernMasters.stream()
                    .map(cm -> new PetConcern(null, savedPet.getId(), cm.getId()))
                    .toList();

            petConcernRepo.saveAll(petConcerns);
        }

        List<AllergenCode> allergyCodes = request.allergies() == null ? List.of() : request.allergies();

        if(!allergyCodes.isEmpty()){
            List<PetAllergy> petAllergies = allergyCodes.stream()
                    .map(code -> new PetAllergy(null, code, savedPet.getId()))
                    .toList();
            petAllergyRepo.saveAll(petAllergies);
        }


        return savedPet.getId();
        }


    }

