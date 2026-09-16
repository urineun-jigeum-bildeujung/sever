package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.in.web.dto.request.AddressRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.AddressUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.AddressSnapshotResponse;
import com.golajugaenyang.member.domain.entity.Address;
import com.golajugaenyang.member.domain.repository.AddressRepository;
import com.golajugaenyang.member.error.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepo;

    @Transactional
    public Address registerAddress(Long memberId, AddressRegisterRequest request){
        boolean isFirstAddress = !addressRepo.existsByMemberId(memberId);
        boolean isDefault = isFirstAddress || request.isDefault();

        if (isDefault && !isFirstAddress) {
            demoteExistingDefault(memberId);
        }

        Address address = new Address(
                null, request.addressName(), request.receiver(), request.phone(), request.zipCode(),
                request.address(), request.addressDetail(), isDefault, request.deliveryNote(), null, null, memberId
        );

        return addressRepo.save(address);
    }

    public List<Address> getMyAddresses(Long memberId) {
        return addressRepo.findAllByMemberId(memberId);
    }

    public AddressSnapshotResponse getAddressSnapshot(Long memberId, Long addressId){
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_ADDRESS));

        if(!address.getMemberId().equals(memberId)){
            throw new AppException(MemberErrorCode.NOT_FOUND_ADDRESS);
        }

        return new AddressSnapshotResponse(address.getAddressName(), address.getReceiver(), address.getReceiverPhone(),
                address.getZipCode(), address.getAddress(), address.getAddressDetail(), address.getDeliveryNote());
    }

    @Transactional
    public void updateAddress(Long memberId, Long addressId, AddressUpdateRequest request) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_ADDRESS));

        if (!address.getMemberId().equals(memberId)) {
            throw new AppException(MemberErrorCode.NOT_FOUND_ADDRESS);
        }

        boolean isDefault = request.isDefault() != null ? request.isDefault() : address.isDefault();

        if (address.isDefault() && !isDefault) {
            throw new AppException(MemberErrorCode.LAST_DEFAULT_ADDRESS);
        }

        if (isDefault && !address.isDefault()) {
            demoteExistingDefault(memberId);
        }

        addressRepo.save(mergeWithRequest(address, addressId, memberId, request, isDefault));
    }

    @Transactional
    public void deleteAddress(Long memberId, Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND_ADDRESS));

        if (!address.getMemberId().equals(memberId)) {
            throw new AppException(MemberErrorCode.NOT_FOUND_ADDRESS);
        }

        if (address.isDefault() && addressRepo.findAllByMemberId(memberId).size() > 1) {
            throw new AppException(MemberErrorCode.LAST_DEFAULT_ADDRESS);
        }

        addressRepo.deleteById(addressId);
    }

    private Address mergeWithRequest(Address existing, Long addressId, Long memberId,
                                      AddressUpdateRequest request, boolean isDefault) {
        return new Address(
                addressId,
                request.addressName() != null ? request.addressName() : existing.getAddressName(),
                request.receiver() != null ? request.receiver() : existing.getReceiver(),
                request.phone() != null ? request.phone() : existing.getReceiverPhone(),
                request.zipCode() != null ? request.zipCode() : existing.getZipCode(),
                request.address() != null ? request.address() : existing.getAddress(),
                request.addressDetail() != null ? request.addressDetail() : existing.getAddressDetail(),
                isDefault,
                request.deliveryNote() != null ? request.deliveryNote() : existing.getDeliveryNote(),
                existing.getCreatedAt(), existing.getUpdatedAt(), memberId
        );
    }

    private Address unsetDefault(Address address){
        return new Address(
                address.getId(), address.getAddressName(), address.getReceiver(), address.getReceiverPhone(),
                address.getZipCode(), address.getAddress(), address.getAddressDetail(), false,
                address.getDeliveryNote(), address.getCreatedAt(), address.getUpdatedAt(), address.getMemberId()
        );
    }

    private void demoteExistingDefault(Long memberId) {
        addressRepo.findDefaultByMemberId(memberId)
                .ifPresent(previousDefault -> addressRepo.save(unsetDefault(previousDefault)));
    }

}
