package com.golajugaenyang.order.application.order.port.out;

import com.golajugaenyang.order.application.order.port.out.dto.AddressInfo;

public interface AddressLookupPort {

    AddressInfo lookup(Long addressId, Long memberId);
}
