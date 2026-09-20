package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.member.adapter.in.web.dto.response.AddressSnapshotResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.MemberIdResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.NicknameResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.PetDetailResponse;
import com.golajugaenyang.member.application.AddressService;
import com.golajugaenyang.member.application.MemberService;
import com.golajugaenyang.member.application.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/v1/members")
@RequiredArgsConstructor
public class InternalMemberController {

    private final MemberService memberService;
    private final AddressService addressService;
    private final PetService petService;

    @GetMapping("/nickname")
    public NicknameResponse getNickname(){
        String nickname = memberService.generateUniqueNickname();
        return new NicknameResponse(nickname);
    }

    @GetMapping("/me")
    public MemberIdResponse getMemberId(@RequestParam Long authId){
        Long memberId = memberService.getMemberIdByAuthId(authId);
        return new MemberIdResponse(memberId);
    }

    @GetMapping("/{memberId}/addresses/{addressId}")
    public AddressSnapshotResponse getAddress(
            @PathVariable Long memberId,
            @PathVariable Long addressId){
        return addressService.getAddressSnapshot(memberId, addressId);
    }

    @GetMapping("/{memberId}/pets/{petId}")
    public PetDetailResponse getPetDetail(
            @PathVariable Long memberId,
            @PathVariable Long petId){
        return petService.getPetDetail(memberId, petId);
    }

}
