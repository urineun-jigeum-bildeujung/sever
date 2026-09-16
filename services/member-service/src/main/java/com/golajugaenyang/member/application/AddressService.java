package com.golajugaenyang.member.application;

import com.golajugaenyang.member.adapter.in.web.dto.request.AddressRegisterRequest;
import com.golajugaenyang.member.domain.entity.Address;
import com.golajugaenyang.member.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepo;

    @Transactional
    public Address registerAddress(Long memberId, AddressRegisterRequest request){
        boolean isFirstAddress = !addressRepo.existsByMemberId(memberId);
        boolean isDefault = isFirstAddress || request.isDefault();

        if (isDefault && !isFirstAddress) {
            addressRepo.findDefaultByMemberId(memberId)
                .ifPresent(previousDefault -> addressRepo.save(unsetDefault(previousDefault)));
        }

        Address address = new Address(
                null, request.addressName(), request.receiver(), request.phone(), request.zipCode(),
                request.address(), request.addressDetail(), isDefault, request.deliveryNote(), null, null, memberId
        );

        return addressRepo.save(address);
    }

    private Address unsetDefault(Address address){
        return new Address(
                address.getId(), address.getAddressName(), address.getReceiver(), address.getReceiverPhone(),
                address.getZipCode(), address.getAddress(), address.getAddressDetail(), false,
                address.getDeliveryNote(), address.getCreatedAt(), address.getUpdatedAt(), address.getMemberId()
        );
    }

}
