package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.OrderDetailResult;

public interface GetOrderDetailUseCase {

    OrderDetailResult getOrderDetail(Long orderId, Long memberId);
}
