package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.AllergyOption;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetDetailResponse;
import com.golajugaenyang.member.domain.entity.BreedMaster;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.entity.PetAllergy;
import com.golajugaenyang.member.domain.entity.PetConcern;
import com.golajugaenyang.member.domain.repository.BreedMasterRepository;
import com.golajugaenyang.member.domain.repository.ConcernMasterRepository;
import com.golajugaenyang.member.domain.repository.PetAllergyRepository;
import com.golajugaenyang.member.domain.repository.PetConcernRepository;
import com.golajugaenyang.member.domain.repository.PetRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.golajugaenyang.member.error.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepo;
    private final PetConcernRepository petConcernRepo;
    private final PetAllergyRepository petAllergyRepo;
    private final ConcernMasterRepository concernMasterRepo;
    private final BreedMasterRepository breedMasterRepo;

    @Transactional
    public Pet registerPet(Long memberId, PetRegisterRequest request) {

        validateBreed(request.breedId(), request.species());

        List<String> concernCodes = request.healthConcerns() == null ? List.of() : request.healthConcerns();
        List<ConcernMaster> concernMasters = validateConcerns(concernCodes, request.species());

        List<AllergenCode> allergyCodes = request.allergies() == null ? List.of() : request.allergies();
        validateAllergies(allergyCodes, request.species());

        boolean isDefault = !petRepo.existsByMemberId(memberId);

        Pet savedPet = petRepo.save(
                new Pet(null, isDefault, request.name(), request.sex(), request.isNeutered(),
                        request.species(), request.age(), request.birthDate(), request.size(),
                        request.weight(), request.bcs(), request.image(), null,
                        memberId, request.breedId(), null, null)
        );

        if (!concernMasters.isEmpty()) {
            List<PetConcern> petConcerns = concernMasters.stream()
                    .map(cm -> new PetConcern(null, savedPet.getId(), cm.getId()))
                    .toList();

            petConcernRepo.saveAll(petConcerns);
        }

        if (!allergyCodes.isEmpty()) {
            List<PetAllergy> petAllergies = allergyCodes.stream()
                    .map(code -> new PetAllergy(null, code, savedPet.getId()))
                    .toList();
            petAllergyRepo.saveAll(petAllergies);
        }

        return savedPet;
    }

    @Transactional
    public Pet updatePet(Long memberId, Long petId, PetUpdateRequest request) {
        Pet pet = petRepo.findByIdForUpdate(petId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_PET));

        if (!pet.getMemberId().equals(memberId)) {
            throw new AppException(MemberErrorCode.NOT_FOUND_PET);
        }

        Species finalSpecies = request.species() != null ? request.species() : pet.getSpecies();
        Long finalBreedId = request.breedId() != null ? request.breedId() : pet.getBreedId();

        if (request.species() != null || request.breedId() != null) {
            validateBreed(finalBreedId, finalSpecies);
        }

        if (request.healthConcerns() != null) {
            List<ConcernMaster> concernMasters = validateConcerns(request.healthConcerns(), finalSpecies);
            petConcernRepo.deleteByPetId(petId);
            if (!concernMasters.isEmpty()) {
                List<PetConcern> petConcerns = concernMasters.stream()
                        .map(cm -> new PetConcern(null, petId, cm.getId()))
                        .toList();
                petConcernRepo.saveAll(petConcerns);
            }
        }

        if (request.allergies() != null) {
            validateAllergies(request.allergies(), finalSpecies);
            petAllergyRepo.deleteByPetId(petId);
            if (!request.allergies().isEmpty()) {
                List<PetAllergy> petAllergies = request.allergies().stream()
                        .map(code -> new PetAllergy(null, code, petId))
                        .toList();
                petAllergyRepo.saveAll(petAllergies);
            }
        }

        Pet updatedPet = pet.update(request.name(), request.sex(), request.isNeutered(), request.species(),
                request.age(), request.birthDate(), request.size(), request.weight(), request.bcs(),
                request.image(), request.breedId());

        return petRepo.save(updatedPet);
    }

    @Transactional
    public void deletePet(Long memberId, Long petId) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_PET));

        if (!pet.getMemberId().equals(memberId)) {
            throw new AppException(MemberErrorCode.NOT_FOUND_PET);
        }

        petRepo.save(pet.delete());

        if (pet.isDefault()) {
            petRepo.findByMemberId(memberId).stream()
                    .findFirst()
                    .ifPresent(nextDefault -> petRepo.save(nextDefault.withIsDefault(true)));
        }
    }

    private void validateBreed(Long breedId, Species species) {
        BreedMaster breed = breedMasterRepo.findById(breedId)
                .orElseThrow(() -> new AppException(MemberErrorCode.INVALID_BREED));

        if (breed.getSpecies() != species) {
            throw new AppException(MemberErrorCode.INVALID_BREED);
        }
    }

    public List<BreedMaster> getBreeds(Species species) {
        return breedMasterRepo.findBySpecies(species);
    }

    public List<ConcernMaster> getConcerns(Species species) {
        return concernMasterRepo.findBySpecies(species);
    }

    public List<AllergenCode> getAllergies(Species species) {
        return Arrays.stream(AllergenCode.values())
                .filter(code -> code.getApplicableSpecies().contains(species))
                .toList();
    }

    public List<Pet> getPets(Long memberId) {
        return petRepo.findByMemberId(memberId);
    }

    public PetDetailResponse getPetDetail(Long memberId, Long petId){
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_PET));

        if(!pet.getMemberId().equals(memberId)){
            throw new AppException(MemberErrorCode.NOT_FOUND_PET);
        }

        BreedMaster breed = breedMasterRepo.findById(pet.getBreedId())
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_PET));

        List<Long> concernIds = petConcernRepo.findByPetId(petId).stream()
                .map(PetConcern::getConcernId)
                .toList();
        List<String> healthConcerns = concernMasterRepo.findByIdIn(concernIds).stream()
                .map(ConcernMaster::getConcernCode)
                .toList();

        List<AllergyOption> allergies = petAllergyRepo.findByPetId(petId).stream()
                .map(pa -> new AllergyOption(pa.getAllergyCode().name(), pa.getAllergyCode().getDisplayName()))
                .toList();

        return new PetDetailResponse(  pet.getId(), pet.getName(), pet.getSpecies(), pet.getBreedId(), breed.getBreedName(),
                pet.getAge(), pet.getBirthDate(), pet.getSex(), pet.isNeutered(), pet.getTargetBreedSize(),
                pet.getWeight(), pet.getBcs(), healthConcerns, allergies, pet.getImage(), pet.isDefault());
    }

    private List<ConcernMaster> validateConcerns(List<String> concernCodes, Species species) {
        if (concernCodes.isEmpty()) {
            return List.of();
        }

        List<ConcernMaster> concernMasters = concernMasterRepo.findByConcernCodeInAndSpecies(concernCodes, species);

        Set<String> foundCodes = concernMasters.stream()
                .map(ConcernMaster::getConcernCode)
                .collect(Collectors.toSet());

        if (!foundCodes.containsAll(concernCodes)) {
            throw new AppException(MemberErrorCode.INVALID_CONCERN);
        }

        return concernMasters;
    }

    private void validateAllergies(List<AllergenCode> allergyCodes, Species species) {
        boolean allApplicable = allergyCodes.stream()
                .allMatch(code -> code.getApplicableSpecies().contains(species));

        if (!allApplicable) {
            throw new AppException(MemberErrorCode.INVALID_ALLERGY);
        }
    }

}
