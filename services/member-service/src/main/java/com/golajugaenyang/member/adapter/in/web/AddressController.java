package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.member.adapter.in.web.dto.request.AddressRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.AddressRegisterResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.AddressSnapshotResponse;
import com.golajugaenyang.member.application.AddressService;
import com.golajugaenyang.member.domain.entity.Address;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;


    @PostMapping
    public ResponseEntity<AddressRegisterResponse> registerAddress(
            @MemberId Long memberId,
            @Valid @RequestBody AddressRegisterRequest request){
        Address savedAddress = addressService.registerAddress(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddressRegisterResponse(savedAddress.getId()));
    }

}
