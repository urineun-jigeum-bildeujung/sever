package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.core.domain.AllergenCode;
import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.adapter.in.web.dto.response.BreedResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetHealthOptionsResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetHealthOptionsResponse.AllergyOption;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetHealthOptionsResponse.CategoryOption;
import com.golajugaenyang.member.application.PetService;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetMasterDataController {

    private final PetService petService;

    @GetMapping("/breeds")
    public ResponseEntity<List<BreedResponse>> getBreeds(@RequestParam Species species) {
        List<BreedResponse> response = petService.getBreeds(species).stream()
                .map(breed -> new BreedResponse(breed.getId(), breed.getBreedName()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health-options")
    public ResponseEntity<PetHealthOptionsResponse> getHealthOptions(@RequestParam Species species) {
        List<CategoryOption> categories = petService.getConcerns(species).stream()
                .collect(Collectors.groupingBy(
                        ConcernMaster::getConcernCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(ConcernMaster::getConcernCode, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new CategoryOption(entry.getKey(), entry.getValue()))
                .toList();

        List<AllergyOption> allergies = petService.getAllergies(species).stream()
                .map(code -> new AllergyOption(code.name(), code.getDisplayName()))
                .toList();

        return ResponseEntity.ok(new PetHealthOptionsResponse(categories, allergies));
    }
}
