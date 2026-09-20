package com.golajugaenyang.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.common.core.domain.TargetBreedSize;
import com.golajugaenyang.common.storage.ObjectTagConfirmer;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.AllergyOption;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetDetailResponse;
import com.golajugaenyang.member.domain.entity.BreedMaster;
import com.golajugaenyang.member.domain.entity.Pet;
import com.golajugaenyang.member.domain.entity.PetAllergy;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import com.golajugaenyang.member.domain.repository.BreedMasterRepository;
import com.golajugaenyang.member.domain.repository.ConcernMasterRepository;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import com.golajugaenyang.member.domain.repository.PetAllergyRepository;
import com.golajugaenyang.member.domain.repository.PetConcernRepository;
import com.golajugaenyang.member.domain.repository.PetRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

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
    @Mock
    private MemberRepository memberRepo;
    @Mock
    private ObjectTagConfirmer objectTagConfirmer;

    @InjectMocks
    private PetService petService;


    @BeforeEach
    void setUpTransactionSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDownTransactionSynchronization() {
        TransactionSynchronizationManager.clearSynchronization();
    }

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

    @Test
    @DisplayName("반려동물 등록 시 image가 있으면 태그를 confirmed로 변경한다.")
    void registerPet_confirms_tag_when_image_present() {
        Long memberId = 1L;
        Long breedId = 100L;
        String fileUrl = "https://image.leechs.shop/profiles/member-1/uuid.jpg";

        PetRegisterRequest request = new PetRegisterRequest(
            "초코", Sex.MALE, false, Species.DOG, 3, LocalDate.of(2021, 1, 1),
            TargetBreedSize.SMALL, 5.2, 4, fileUrl, breedId, null, null);

        when(breedMasterRepo.findById(breedId))
            .thenReturn(Optional.of(new BreedMaster(breedId, Species.DOG, "포메라니안")));
        when(petRepo.existsByMemberId(memberId)).thenReturn(false);
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        petService.registerPet(memberId, request);

        verify(objectTagConfirmer).validateOwnership(fileUrl, "member-" + memberId);
        verify(objectTagConfirmer, never()).confirm(any(), any());

        TransactionSynchronizationUtils.triggerAfterCommit();

        verify(objectTagConfirmer).confirm(fileUrl, "member-" + memberId);
    }

    @Test
    @DisplayName("반려동물 수정 시 image가 있으면 태그를 confirmed로 변경한다.")
    void updatePet_confirms_tag_when_image_present() {
        Long memberId = 1L;
        Long petId = 10L;
        String fileUrl = "https://image.leechs.shop/profiles/member-1/uuid.jpg";

        Pet pet = new Pet(petId, true, "초코", Sex.MALE, false, Species.DOG, 3,
            LocalDate.of(2021, 1, 1), TargetBreedSize.SMALL, 5.2, 4, null, null,
            memberId, 100L, null, null);

        PetUpdateRequest request = new PetUpdateRequest(
            null, null, null, null, null, null, null, null, null,
            fileUrl, null, null, null);

        when(petRepo.findByIdForUpdate(petId)).thenReturn(Optional.of(pet));
        when(petRepo.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        petService.updatePet(memberId, petId, request);

        verify(objectTagConfirmer).validateOwnership(fileUrl, "member-" + memberId);
        verify(objectTagConfirmer, never()).confirm(any(), any());

        TransactionSynchronizationUtils.triggerAfterCommit();

        verify(objectTagConfirmer).confirm(fileUrl, "member-" + memberId);
    }
}
