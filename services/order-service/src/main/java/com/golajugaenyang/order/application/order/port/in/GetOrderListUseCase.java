package com.golajugaenyang.order.application.order.port.in;

import com.golajugaenyang.order.application.order.port.in.dto.GetOrderListQuery;
import com.golajugaenyang.order.application.order.port.in.dto.OrderListResult;

public interface GetOrderListUseCase {

    OrderListResult getOrderList(GetOrderListQuery query);
}
