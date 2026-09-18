package com.golajugaenyang.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.member.adapter.in.web.dto.response.AllergyOption;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetDetailResponse;
import com.golajugaenyang.member.domain.entity.BreedMaster;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.entity.PetAllergy;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import com.golajugaenyang.member.domain.repository.BreedMasterRepository;
import com.golajugaenyang.member.domain.repository.ConcernMasterRepository;
import com.golajugaenyang.member.domain.repository.PetAllergyRepository;
import com.golajugaenyang.member.domain.repository.PetConcernRepository;
import com.golajugaenyang.member.domain.repository.PetRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepo;
    @Mock
    private PetConcernRepository petConcernRepo;
    @Mock
    private PetAllergyRepository petAllergyRepo;
    @Mock
    private ConcernMasterRepository concernMasterRepo;
    @Mock
    private BreedMasterRepository breedMasterRepo;

    @InjectMocks
    private PetService petService;

    @Test
    @DisplayName("반려동물 상세 조회 시 allergies는 code와 displayName을 함께 반환한다.")
    void returns_allergies_with_code_and_display_name() {
        Long memberId = 1L;
        Long petId = 10L;

        Pet pet = new Pet(petId, true, "초코", Sex.MALE, false, Species.DOG, 3,
            LocalDate.of(2021, 1, 1), TargetBreedSize.SMALL, 5.2, 4, null, null,
            memberId, 100L, null, null);

        when(petRepo.findById(petId)).thenReturn(Optional.of(pet));
        when(breedMasterRepo.findById(100L))
            .thenReturn(Optional.of(new BreedMaster(100L, Species.DOG, "포메라니안")));
        when(petConcernRepo.findByPetId(petId)).thenReturn(List.of());
        when(concernMasterRepo.findByIdIn(List.of())).thenReturn(List.of());
        when(petAllergyRepo.findByPetId(petId))
            .thenReturn(List.of(new PetAllergy(1L, AllergenCode.CHICKEN, petId)));

        PetDetailResponse response = petService.getPetDetail(memberId, petId);

        assertThat(response.allergies())
            .containsExactly(new AllergyOption("CHICKEN", "닭고기"));
    }
}
