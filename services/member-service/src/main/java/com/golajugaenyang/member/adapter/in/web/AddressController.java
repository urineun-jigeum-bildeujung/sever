package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.member.adapter.in.web.dto.request.AddressRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.AddressDetailResponse;
import com.golajugaenyang.member.adapter.in.web.dto.response.AddressRegisterResponse;
import com.golajugaenyang.member.application.AddressService;
import com.golajugaenyang.member.domain.entity.Address;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<AddressDetailResponse>> getMyAddresses(@MemberId Long memberId){
        List<AddressDetailResponse> responses = addressService.getMyAddresses(memberId).stream()
                .map(address -> new AddressDetailResponse(address.getId(), address.getAddressName(),
                        address.getReceiver(), address.getReceiverPhone(),
                        address.getZipCode(), address.getAddress(), address.getAddressDetail(),
                        address.getDeliveryNote(), address.isDefault()))
                .toList();
        return ResponseEntity.ok(responses);
    }

}
