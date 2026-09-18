package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.security.annotation.AuthId;
import com.golajugaenyang.member.adapter.in.web.dto.request.MemberProfileUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PhoneRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.SignupRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.*;
import com.golajugaenyang.member.adapter.out.client.dto.TokenPairResponse;
import com.golajugaenyang.member.application.MemberService;
import com.golajugaenyang.member.application.PetService;
import com.golajugaenyang.member.domain.entity.Pet;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PetService petService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
        @AuthId Long authId,
        @Valid @RequestBody SignupRequest request
    ) {
        TokenPairResponse tokenPair = memberService.signUp(authId, request.nickname(), request.agreements());
        SignupResponse response = new SignupResponse(tokenPair.accessToken(), tokenPair.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/me/pets")
    public ResponseEntity<PetRegisterResponse> registerPet(
            @AuthId Long authId,
            @Valid @RequestBody PetRegisterRequest request
            ) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        Pet savedPet = petService.registerPet(memberId, request);
        PetRegisterResponse response = new PetRegisterResponse(
                savedPet.getId(), savedPet.getName(), savedPet.getSpecies(), savedPet.isDefault(), savedPet.getBreedId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me/pets")
    public ResponseEntity<List<PetSummaryResponse>> getMyPets(@AuthId Long authId) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        List<PetSummaryResponse> response = petService.getPets(memberId).stream()
                .map(pet -> new PetSummaryResponse(pet.getId(), pet.getName(), pet.getImage(), pet.isDefault()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/pets/{petId}")
    public ResponseEntity<PetDetailResponse> getMyPet(
            @AuthId Long authId,
            @PathVariable Long petId){
        Long memberId = memberService.getMemberIdByAuthId(authId);
        PetDetailResponse response = petService.getPetDetail(memberId, petId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/pets/{petId}")
    public ResponseEntity<PetRegisterResponse> updatePet(
            @AuthId Long authId,
            @PathVariable Long petId,
            @Valid @RequestBody PetUpdateRequest request) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        Pet updatedPet = petService.updatePet(memberId, petId, request);
        PetRegisterResponse response = new PetRegisterResponse(
                updatedPet.getId(), updatedPet.getName(), updatedPet.getSpecies(), updatedPet.isDefault(), updatedPet.getBreedId()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/pets/{petId}")
    public ResponseEntity<Void> deletePet(
            @AuthId Long authId,
            @PathVariable Long petId) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        petService.deletePet(memberId, petId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/phone")
    public ResponseEntity<Void> registerPhone(
            @AuthId Long authId,
            @Valid @RequestBody PhoneRegisterRequest request) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        memberService.registerPhone(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberMyProfileResponse> getMyProfile(
            @AuthId Long authId) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        MemberMyProfileResponse response = memberService.getMyProfile(authId, memberId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthId Long authId,
            @Valid @RequestBody MemberProfileUpdateRequest request) {
        Long memberId = memberService.getMemberIdByAuthId(authId);
        memberService.updateProfile(memberId, request);
        return ResponseEntity.noContent().build();
    }
}
