package com.golajugaenyang.order.adapter.out.external.member;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.adapter.out.external.member.client.MemberInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.member.dto.AddressSnapshotResponse;
import com.golajugaenyang.order.application.order.port.out.AddressLookupPort;
import com.golajugaenyang.order.application.order.port.out.dto.AddressInfo;
import com.golajugaenyang.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;



@Component
@RequiredArgsConstructor
public class MemberAddressLookupAdapter implements AddressLookupPort {

    private final MemberInternalApiClient memberInternalApiClient;

    @Override
    public AddressInfo lookup(Long addressId, Long memberId) {
        ResponseEntity<AddressSnapshotResponse> response;
        try {
            response = memberInternalApiClient.getAddress(memberId, addressId);
        } catch (RestClientException e) {
            throw new AppException(OrderErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        AddressSnapshotResponse body = response.getBody();
        if (body == null) {
            throw new AppException(OrderErrorCode.ADDRESS_NOT_FOUND);
        }
        return new AddressInfo(
            body.addressName(), body.receiver(), body.receiverPhone(),
            body.zipCode(), body.address(), body.addressDetail(),
            body.deliveryNote());
    }
}
