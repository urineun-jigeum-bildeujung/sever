package com.golajugaenyang.order.adapter.out.external.member;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.order.adapter.out.external.member.client.MemberInternalApiClient;
import com.golajugaenyang.order.adapter.out.external.member.dto.AddressSnapshotResponse;
import com.golajugaenyang.order.application.order.port.out.AddressLookupPort;
import com.golajugaenyang.order.application.order.port.out.dto.AddressInfo;
import com.golajugaenyang.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;


@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAddressLookupAdapter implements AddressLookupPort {

    private final MemberInternalApiClient memberInternalApiClient;

    @Override
    public AddressInfo lookup(Long addressId, Long memberId) {
        ResponseEntity<AddressSnapshotResponse> response;
        try {
            response = memberInternalApiClient.getAddress(memberId, addressId);
        } catch (ResourceAccessException e) {
            throw new AppException(OrderErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new AppException(OrderErrorCode.MEMBER_SERVICE_UNAVAILABLE);
            }
            log.error("[MemberAddressLookup] 예상 못한 4xx 응답. status={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new AppException(OrderErrorCode.MEMBER_SERVICE_REQUEST_INVALID);
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
