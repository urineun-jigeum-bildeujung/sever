package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.security.annotation.AuthId;
import com.golajugaenyang.member.adapter.in.web.dto.request.PetRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.SignupRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetRegisterResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetSummaryResponse;
import com.golajugaenyang.member.application.MemberService;
import com.golajugaenyang.member.application.PetService;
import com.golajugaenyang.member.domain.entity.Pet;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PetService petService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
        @AuthId Long authId,
        @Valid @RequestBody SignupRequest request
    ) {
        memberService.signUp(authId, request.nickname(), request.agreements());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

}
